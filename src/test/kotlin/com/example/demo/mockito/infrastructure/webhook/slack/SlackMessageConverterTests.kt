package com.example.demo.mockito.infrastructure.webhook.slack

import com.example.demo.infrastructure.webhook.common.CommonWebHookMessage
import com.example.demo.infrastructure.webhook.slack.SlackMessage
import com.example.demo.infrastructure.webhook.slack.SlackMessageConverter
import com.example.demo.infrastructure.webhook.slack.SlackWebHookMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("mockito-unit-test")
@DisplayName("Mockito Unit - Slack Message Converter Test")
@ExtendWith(
	MockitoExtension::class
)
class SlackMessageConverterTests {
	private val converter = SlackMessageConverter()

	@Test
	fun `convertCommonToSlackMessage should convert CommonWebHookMessage to SlackWebHookMessage`() {
		val commonMessage =
			CommonWebHookMessage(
				title = "Test Title",
				contents = listOf("Line 1", "Line 2")
			)

		val result = converter.convertCommonToSlackMessage(commonMessage)

		assertEquals(1, result.messages.size)
		val message = result.messages[0]
		assertEquals("Test Title", message.title)
		assertEquals(listOf("Line 1", "Line 2"), message.messages)
	}

	@Test
	fun `convertToSlackMessage should return the same SlackWebHookMessage instance`() {
		val slackMessage =
			SlackWebHookMessage(
				mutableListOf(SlackMessage("Alert", mutableListOf("msg1", "msg2")))
			)

		val result = converter.convertToSlackMessage(slackMessage)

		assertSame(slackMessage, result)
	}
}
