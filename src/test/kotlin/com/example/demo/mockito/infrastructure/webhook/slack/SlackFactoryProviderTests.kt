package com.example.demo.mockito.infrastructure.webhook.slack

import com.example.demo.infrastructure.webhook.slack.SlackFactoryProvider
import com.example.demo.infrastructure.webhook.slack.SlackMessage
import com.slack.api.Slack
import com.slack.api.model.block.DividerBlock
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.webhook.Payload
import com.slack.api.webhook.WebhookResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("mockito-unit-test")
@DisplayName("Mockito Unit - Slack Factory Provider Test")
@ExtendWith(
	MockitoExtension::class
)
class SlackFactoryProviderTests {
	@Mock
	private lateinit var slackMock: Slack

	@Mock
	private lateinit var responseMock: WebhookResponse
	
	private lateinit var slackStaticMock: MockedStatic<Slack>
	private lateinit var provider: SlackFactoryProvider

	private val url = "https://hooks.slack.com/services/test"

	@BeforeEach
	fun setup() {
		slackStaticMock = mockStatic(Slack::class.java)
		slackStaticMock.`when`<Slack> { Slack.getInstance() }.thenReturn(slackMock)

		provider = SlackFactoryProvider(url)
	}

	@AfterEach
	fun tearDown() {
		slackStaticMock.close()
	}

	@Test
	fun `should send message successfully when Slack responds with 200`() {
		`when`(responseMock.code).thenReturn(200)
		`when`(slackMock.send(eq(url), any<Payload>())).thenReturn(responseMock)

		val slackMessage =
			listOf(
				SlackMessage("Test Title", mutableListOf("Line1", "Line2"))
			)

		provider.send(slackMessage)

		verify(slackMock).send(eq(url), any<Payload>())
	}

	@Test
	fun `should throw exception when Slack responds with error code`() {
		`when`(responseMock.code).thenReturn(500)
		`when`(responseMock.message).thenReturn("Internal Server Error")
		`when`(slackMock.send(eq(url), any<Payload>())).thenReturn(responseMock)

		val slackMessage =
			listOf(
				SlackMessage("Test Title", mutableListOf("Line1"))
			)

		val exception =
			kotlin
				.runCatching {
					provider.send(slackMessage)
				}.exceptionOrNull()

		assert(exception is IllegalArgumentException)
		assert(exception?.message == "Slack response code: 500, message: Internal Server Error")

		verify(slackMock).send(eq(url), any<Payload>())
	}

	@Test
	fun `generatePayload should create valid Payload with blocks and formatted text`() {
		fun sectionBlock(text: String): SectionBlock =
			SectionBlock
				.builder()
				.text(MarkdownTextObject.builder().text(text).build())
				.build()

		fun dividerBlock(): DividerBlock = DividerBlock()

		fun buildMessageBlock(
			dto: SlackMessage,
			isLast: Boolean
		): List<LayoutBlock> {
			val blocks = mutableListOf<LayoutBlock>()
			blocks.add(sectionBlock("*:rotating_light: ${dto.title}*"))
			blocks.add(dividerBlock())
			blocks.addAll(dto.messages.map { sectionBlock("• $it") })
			if (!isLast) blocks.add(dividerBlock())
			return blocks
		}

		fun generatePayload(slackMessage: List<SlackMessage>): Payload {
			val blocks =
				slackMessage.flatMapIndexed { index, dto ->
					buildMessageBlock(dto, isLast = index == slackMessage.lastIndex)
				}

			val text =
				slackMessage.joinToString("\n\n") { dto ->
					"*${dto.title}*\n" + dto.messages.joinToString("\n") { "• $it" }
				}

			return Payload
				.builder()
				.text(text)
				.blocks(blocks)
				.build()
		}

		val messages =
			listOf(
				SlackMessage("System", mutableListOf("CPU High", "Memory Low")),
				SlackMessage("Database", mutableListOf("Connection lost", "Slow query"))
			)

		val payload = generatePayload(messages)

		val expectedText =
			"""
			*System*
			• CPU High
			• Memory Low

			*Database*
			• Connection lost
			• Slow query
			""".trimIndent()

		assertEquals(expectedText, payload.text)
		assertEquals(9, payload.blocks.size)

		val firstBlock = payload.blocks[0] as SectionBlock
		assertEquals("*:rotating_light: System*", firstBlock.text.text)
	}
}
