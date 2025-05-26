package com.example.demo.mockito.infrastructure.webhook.slack

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.common.WebHookMessage
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.slack.SlackFactoryProvider
import com.example.demo.infrastructure.webhook.slack.SlackMessage
import com.example.demo.infrastructure.webhook.slack.SlackWebHookMessage
import com.example.demo.infrastructure.webhook.slack.SlackWebHookSender
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("mockito-unit-test")
@DisplayName("Mockito Unit - Slack WebHook Sender Test")
@ExtendWith(
	MockitoExtension::class
)
class SlackWebHookSenderTests {
	@Mock
	private lateinit var slackFactoryProvider: SlackFactoryProvider
	private lateinit var sender: SlackWebHookSender

	@BeforeEach
	fun setup() {
		sender = SlackWebHookSender(slackFactoryProvider)
	}

	@Test
	fun `should return SLACK as target`() {
		val result = sender.target()
		assertEquals(WebHookTarget.SLACK, result)
	}

	@Test
	fun `should send SlackWebHookMessage to SlackFactoryProvider`() {
		val message = SlackMessage("Test Title", mutableListOf("msg1", "msg2"))
		val slackWebHookMessage = SlackWebHookMessage(mutableListOf(message))

		sender.send(slackWebHookMessage)

		verify(slackFactoryProvider, times(1)).send(slackWebHookMessage.messages)
	}

	@Test
	fun `should throw exception when message is not SlackWebHookMessage`() {
		val invalidMessage = object : WebHookMessage {}

		val exception =
			assertThrows(CustomRuntimeException::class.java) {
				sender.send(invalidMessage)
			}

		assertEquals("Invalid message type for Slack", exception.message)
		verify(slackFactoryProvider, never()).send(any<List<SlackMessage>>())
	}
}
