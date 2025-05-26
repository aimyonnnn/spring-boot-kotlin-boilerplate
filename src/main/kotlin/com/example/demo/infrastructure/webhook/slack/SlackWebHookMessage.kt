package com.example.demo.infrastructure.webhook.slack

import com.example.demo.infrastructure.webhook.common.WebHookMessage

data class SlackWebHookMessage(
	val messages: List<SlackMessage>
) : WebHookMessage
