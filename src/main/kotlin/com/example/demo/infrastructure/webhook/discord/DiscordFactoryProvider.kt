package com.example.demo.infrastructure.webhook.discord

import com.example.demo.infrastructure.webhook.common.EmojiResolver.resolveLineEmoji
import com.example.demo.infrastructure.webhook.common.EmojiResolver.resolveTitleEmoji
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
}
