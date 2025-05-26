package com.example.demo.mockito.infrastructure.webhook

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.WebHookMessageConverter
import com.example.demo.infrastructure.webhook.common.CommonWebHookMessage
import com.example.demo.infrastructure.webhook.common.WebHookMessage
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.slack.SlackMessage
import com.example.demo.infrastructure.webhook.slack.SlackMessageConverter
import com.example.demo.infrastructure.webhook.slack.SlackWebHookMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("mockito-unit-test")
@DisplayName("Mockito Unit - WebHook Message Converter Test")
@ExtendWith(
	MockitoExtension::class
)
class WebHookMessageConverterTests {
	private lateinit var slackConverter: SlackMessageConverter
	private lateinit var converter: WebHookMessageConverter

	private val slackMessage = SlackWebHookMessage(mutableListOf(SlackMessage("line1", mutableListOf("line2"))))
	private val commonMessage = CommonWebHookMessage("title", listOf("line1", "line2"))
	private val convertedSlackMessage = SlackWebHookMessage(mutableListOf(SlackMessage("converted line", mutableListOf("converted line2"))))

	@BeforeEach
	fun setup() {
		slackConverter = mock(SlackMessageConverter::class.java)
		converter = WebHookMessageConverter(slackConverter)
	}

	@Test
	fun `should convert SlackWebHookMessage when target is SLACK`() {
		`when`(slackConverter.convertToSlackMessage(slackMessage)).thenReturn(convertedSlackMessage)

		val result = converter.convert(WebHookTarget.SLACK, slackMessage)

		assertEquals(convertedSlackMessage, result)
		verify(slackConverter).convertToSlackMessage(slackMessage)
	}

	@Test
	fun `should convert CommonWebHookMessage to Slack message when target is SLACK`() {
		`when`(slackConverter.convertCommonToSlackMessage(commonMessage)).thenReturn(convertedSlackMessage)

		val result = converter.convert(WebHookTarget.SLACK, commonMessage)

		assertEquals(convertedSlackMessage, result)
		verify(slackConverter).convertCommonToSlackMessage(commonMessage)
	}

	@Test
	fun `should throw exception when unknown message type is passed for SLACK`() {
		val unknownMessage = object : WebHookMessage {}

		val exception =
			assertThrows<CustomRuntimeException> {
				converter.convert(WebHookTarget.SLACK, unknownMessage)
			}

		val message = requireNotNull(exception.message)

		assertTrue(message.startsWith("Unsupported message type: class"))
		assertTrue(message.endsWith("for Slack"))
	}

	@Test
	fun `should throw exception when target is unsupported`() {
		val exception =
			assertThrows<CustomRuntimeException> {
				converter.convert(WebHookTarget.DISCORD, commonMessage)
			}

		assertEquals("Unsupported target: DISCORD", exception.message)
	}
}
