package com.example.demo.infrastructure.webhook.slack

data class SlackMessage(
	val title: String,
	val messages: List<String>
) {
	companion object {
		fun of(
			title: String,
			messages: List<String>
		): SlackMessage = SlackMessage(title, messages)
	}
}
