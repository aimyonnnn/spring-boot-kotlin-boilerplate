package com.example.demo.infrastructure.webhook

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.common.CommonWebHookMessage
import com.example.demo.infrastructure.webhook.common.WebHookMessage
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.discord.DiscordMessageConverter
import com.example.demo.infrastructure.webhook.discord.DiscordWebHookMessage
import com.example.demo.infrastructure.webhook.slack.SlackMessageConverter
import com.example.demo.infrastructure.webhook.slack.SlackWebHookMessage
import org.springframework.stereotype.Component

@Component
class WebHookMessageConverter(
	private val slackMessageConverter: SlackMessageConverter,
	private val discordMessageConverter: DiscordMessageConverter
) {
	fun convert(
		target: WebHookTarget,
		message: WebHookMessage
	): WebHookMessage =
		when (target) {
			WebHookTarget.SLACK -> {
				when (message) {
					is SlackWebHookMessage -> slackMessageConverter.convertToSlackMessage(message)
					is CommonWebHookMessage -> slackMessageConverter.convertCommonToSlackMessage(message)
					else -> throw CustomRuntimeException("Unsupported message type: ${message::class} for Slack")
				}
			}

			WebHookTarget.DISCORD -> {
				when (message) {
					is DiscordWebHookMessage -> discordMessageConverter.convertToDiscordMessage(message)
					is CommonWebHookMessage -> discordMessageConverter.convertCommonToDiscordMessage(message)
					else -> throw CustomRuntimeException("Unsupported message type: ${message::class} for Discord")
				}
			}

			else -> throw CustomRuntimeException("Unsupported target: $target")
		}
}
