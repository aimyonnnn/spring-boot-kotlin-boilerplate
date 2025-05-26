package com.example.demo.infrastructure.webhook

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.common.WebHookSender
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.slack.SlackWebHookSender
import org.springframework.stereotype.Component

@Component
class WebHookRouter(
	private val slackSender: SlackWebHookSender
) {
	fun route(webHookTarget: WebHookTarget): WebHookSender =
		when (webHookTarget) {
			WebHookTarget.SLACK -> slackSender
			WebHookTarget.DISCORD -> throw CustomRuntimeException("The target is not implemented yet: $webHookTarget")
			else -> throw CustomRuntimeException("Unsupported target: $webHookTarget")
		}

	fun all(): List<WebHookSender> =
		listOf(
			slackSender
		)
}
