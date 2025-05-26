package com.example.demo.infrastructure.webhook.common

data class CommonWebHookMessage(
	val title: String,
	val contents: List<String>
) : WebHookMessage
