package com.example.demo.infrastructure.webhook

import com.example.demo.infrastructure.webhook.common.CommonWebHookMessage
import com.example.demo.infrastructure.webhook.common.WebHookMessage
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.slack.SlackMessage
import com.example.demo.infrastructure.webhook.slack.SlackWebHookMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class WebHookProvider(
	private val webhookRouter: WebHookRouter,
	private val webhookMessageConverter: WebHookMessageConverter,
	@Value("\${webhook.enabled}") private val enabled: Boolean
) {
	fun sendAll(
		title: String,
		lines: List<String>
	) {
		val commonMessage = createCommonMessage(title, lines)
		this.send(WebHookTarget.ALL, commonMessage)
	}

	fun sendSlack(
		title: String,
		lines: List<String>
	) {
		val slackMessage = createSlackMessage(title, lines)
		this.send(WebHookTarget.SLACK, slackMessage)
	}

	private fun send(
		target: WebHookTarget,
		message: WebHookMessage
	) {
		validateEnabled()

		if (target == WebHookTarget.ALL) {
			require(message is CommonWebHookMessage) {
				"When using WebHookTarget.ALL, message must be of type CommonWebHookMessage"
			}
			sendToAllChannels(message)
			return
		}

		sendToTarget(target, message)
	}

	private fun sendToTarget(
		target: WebHookTarget,
		message: WebHookMessage
	) {
		val sender = webhookRouter.route(target)
		val converted = webhookMessageConverter.convert(target, message)
		sender.send(converted)
	}

	private fun sendToAllChannels(message: CommonWebHookMessage) {
		webhookRouter.all().forEach { sender ->
			val converted = webhookMessageConverter.convert(sender.target(), message)
			sender.send(converted)
		}
	}

	private fun validateEnabled() {
		check(enabled) { "Webhook is not enabled" }
	}

	private fun createSlackMessage(
		title: String,
		lines: List<String>
	): SlackWebHookMessage =
		SlackWebHookMessage(
			mutableListOf(SlackMessage.of(title, lines))
		)

	private fun createCommonMessage(
		title: String,
		lines: List<String>
	): CommonWebHookMessage = CommonWebHookMessage(title, lines)
}
