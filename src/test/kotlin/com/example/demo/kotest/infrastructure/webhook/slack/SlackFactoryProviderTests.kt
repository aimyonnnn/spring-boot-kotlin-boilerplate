package com.example.demo.kotest.infrastructure.webhook.slack

import com.example.demo.infrastructure.webhook.slack.SlackFactoryProvider
import com.example.demo.infrastructure.webhook.slack.SlackMessage
import com.slack.api.Slack
import com.slack.api.model.block.DividerBlock
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.webhook.Payload
import com.slack.api.webhook.WebhookResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class SlackFactoryProviderTests :
	FunSpec({
		val slackClient = mockk<Slack>()
		val response = mockk<WebhookResponse>()
		val slackMessage =
			listOf(
				SlackMessage(
					title = "Test Title",
					messages = mutableListOf("Line1", "Line2")
				)
			)

		mockkStatic(Slack::class)

		every { Slack.getInstance() } returns slackClient

		val url = "https://hooks.slack.com/services/test"
		val provider = SlackFactoryProvider(url)

		test("should send message successfully when Slack responds with 200") {

			every { slackClient.send(any<String>(), any<Payload>()) } returns response

			every { response.code } returns 200

			every { response.message } returns "OK"

			provider.send(slackMessage)

			verify { slackClient.send(url, any<Payload>()) }
		}

		test("should throw exception when Slack responds with error code") {

			every { slackClient.send(any<String>(), any<Payload>()) } returns response

			every { response.code } returns 500

			every { response.message } returns "Internal Server Error"

			val exception =
				shouldThrow<IllegalArgumentException> {
					provider.send(slackMessage)
				}

			exception.message shouldBe "Slack response code: 500, message: Internal Server Error"
		
			verify { slackClient.send(url, any<Payload>()) }
		}

		test("generatePayload() should create valid Payload with blocks and formatted text") {
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

			payload.text shouldBe
				"""
				*System*
				• CPU High
				• Memory Low

				*Database*
				• Connection lost
				• Slow query
				""".trimIndent()

			payload.blocks shouldHaveSize (9)

			val firstBlock = payload.blocks[0] as SectionBlock
			firstBlock.text.text shouldBe "*:rotating_light: System*"
		}
	})
