package com.example.demo.infrastructure.webhook.slack

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.common.WebHookMessage
import com.example.demo.infrastructure.webhook.common.WebHookSender
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import org.springframework.stereotype.Component

@Component
class SlackWebHookSender(
	private val slackFactoryProvider: SlackFactoryProvider
) : WebHookSender {
	override fun target() = WebHookTarget.SLACK

	override fun send(message: WebHookMessage) {
		val slackMessage =
			message as? SlackWebHookMessage
				?: throw CustomRuntimeException("Invalid message type for Slack")

		slackFactoryProvider.send(slackMessage.messages)
	}
}
