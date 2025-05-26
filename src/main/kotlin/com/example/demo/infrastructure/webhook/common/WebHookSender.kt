package com.example.demo.infrastructure.webhook.common

import com.example.demo.infrastructure.webhook.constant.WebHookTarget

interface WebHookSender {
	fun send(message: WebHookMessage)

	fun target(): WebHookTarget
}
