package com.example.demo.infrastructure.webhook.discord

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.common.WebHookMessage
import com.example.demo.infrastructure.webhook.common.WebHookSender
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import org.springframework.stereotype.Component

@Component
class DiscordWebHookSender(
	private val discordFactoryProvider: DiscordFactoryProvider
) : WebHookSender {
	override fun target() = WebHookTarget.DISCORD

	override fun send(message: WebHookMessage) {
		val discordMessage =
			message as? DiscordWebHookMessage
				?: throw CustomRuntimeException("Invalid message type for Discord")

		discordFactoryProvider.send(discordMessage.messages)
	}
}
