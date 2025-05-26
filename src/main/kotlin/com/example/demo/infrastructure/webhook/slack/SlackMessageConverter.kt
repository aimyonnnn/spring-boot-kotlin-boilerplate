package com.example.demo.infrastructure.webhook.slack

import com.example.demo.infrastructure.webhook.common.CommonWebHookMessage
import org.springframework.stereotype.Component

@Component
class SlackMessageConverter {
	fun convertCommonToSlackMessage(message: CommonWebHookMessage): SlackWebHookMessage {
		val slackMessage =
			SlackWebHookMessage(
				mutableListOf(SlackMessage.of(message.title, message.contents))
			)
		return slackMessage
	}

	fun convertToSlackMessage(message: SlackWebHookMessage): SlackWebHookMessage = message
}
