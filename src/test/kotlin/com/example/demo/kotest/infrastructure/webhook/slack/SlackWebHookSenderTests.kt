package com.example.demo.kotest.infrastructure.webhook.slack

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.common.WebHookMessage
import com.example.demo.infrastructure.webhook.slack.SlackFactoryProvider
import com.example.demo.infrastructure.webhook.slack.SlackMessage
import com.example.demo.infrastructure.webhook.slack.SlackWebHookMessage
import com.example.demo.infrastructure.webhook.slack.SlackWebHookSender
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class SlackWebHookSenderTests :
	StringSpec({

		val slackFactoryProvider = mockk<SlackFactoryProvider>()
		val sender = SlackWebHookSender(slackFactoryProvider)

		"should return SLACK as target" {
			sender.target().name shouldBe "SLACK"
		}

		"should send SlackWebHookMessage to SlackFactoryProvider" {
			val slackMessage =
				SlackWebHookMessage(
					mutableListOf(
						SlackMessage("Test Title", mutableListOf("msg1", "msg2"))
					)
				)

			every { slackFactoryProvider.send(any<List<SlackMessage>>()) } just Runs

			sender.send(slackMessage)

			verify(exactly = 1) { slackFactoryProvider.send(slackMessage.messages) }
		}

		"should throw CustomRuntimeException when message is not SlackWebHookMessage" {
			val invalidMessage = object : WebHookMessage {}

			val exception =
				shouldThrow<CustomRuntimeException> {
					sender.send(invalidMessage)
				}

			exception.message shouldBe "Invalid message type for Slack"
		}
	})
