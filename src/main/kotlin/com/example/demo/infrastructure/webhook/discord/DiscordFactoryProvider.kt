package com.example.demo.infrastructure.webhook.discord

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

private val logger = KotlinLogging.logger {}

@Component
class DiscordFactoryProvider(
	@Value("\${webhook.discord.url}") private val url: String,
	private val webClient: WebClient
) {
	fun send(discordMessage: List<DiscordMessage>) {
		webClient
			.post()
			.uri(url)
			.bodyValue(generatePayload(discordMessage))
			.retrieve()
			.bodyToMono(String::class.java)
			.doOnNext {
				logger.info { "webhook (discord) async method started on thread: ${Thread.currentThread().name}" }
			}.doOnError { exception ->
				logger.error(exception) { "Failed to send message to Discord" }
			}.subscribe()
	}

	private fun generatePayload(messages: List<DiscordMessage>): Map<String, Any> {
		val content = messages.joinToString("\n\n", transform = ::formatMessage)
		val embeds = messages.mapNotNull { it.embeds }.flatten()

		return buildMap {
			put("content", content)
			if (embeds.isNotEmpty()) put("embeds", embeds)
		}
	}

	private fun formatMessage(message: DiscordMessage): String {
		val titleEmoji = resolveTitleEmoji(message.title)
		val header = "$titleEmoji **[${message.title}]**"

		val body =
			message.messages.joinToString("\n") { line ->
				"${resolveLineEmoji(line)} $line"
			}

		return "$header\n$body"
	}

	private fun resolveTitleEmoji(title: String): String =
		when {
			title.contains("error", ignoreCase = true) ||
				title.contains("fail", ignoreCase = true) -> "❌"

			title.contains("deploy", ignoreCase = true) ||
				title.contains("release", ignoreCase = true) -> "🚀"

			title.contains("warn", ignoreCase = true) -> "⚠️"
			else -> "📝"
		}

	private fun resolveLineEmoji(line: String): String =
		when {
			line.contains("success", ignoreCase = true) ||
				line.contains("completed", ignoreCase = true) -> "✅"

			line.contains("error", ignoreCase = true) ||
				line.contains("fail", ignoreCase = true) -> "❌"

			line.contains("warn", ignoreCase = true) ||
				line.contains("slow", ignoreCase = true) -> "⚠️"

			else -> "🔹"
		}
}
