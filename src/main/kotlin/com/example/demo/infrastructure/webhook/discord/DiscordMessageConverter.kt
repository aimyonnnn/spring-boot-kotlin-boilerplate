package com.example.demo.infrastructure.webhook.discord

import com.example.demo.infrastructure.webhook.common.CommonWebHookMessage
import org.springframework.stereotype.Component

@Component
class DiscordMessageConverter {
	fun convertCommonToDiscordMessage(message: CommonWebHookMessage): DiscordWebHookMessage {
		val discordMessage =
			DiscordWebHookMessage(
				mutableListOf(DiscordMessage.of(message.title, message.contents))
			)
		
		return discordMessage
	}

	fun convertToDiscordMessage(message: DiscordWebHookMessage): DiscordWebHookMessage = message
}
