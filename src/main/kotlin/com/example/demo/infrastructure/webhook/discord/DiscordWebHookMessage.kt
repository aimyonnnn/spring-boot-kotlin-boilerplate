package com.example.demo.infrastructure.webhook.discord

import com.example.demo.infrastructure.webhook.common.WebHookMessage

data class DiscordWebHookMessage(
	val messages: List<DiscordMessage>
) : WebHookMessage
