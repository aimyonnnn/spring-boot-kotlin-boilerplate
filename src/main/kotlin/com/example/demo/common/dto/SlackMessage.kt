package com.example.demo.common.dto

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
