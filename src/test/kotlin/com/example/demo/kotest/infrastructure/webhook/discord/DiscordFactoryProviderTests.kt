package com.example.demo.kotest.infrastructure.webhook.discord

import com.example.demo.infrastructure.webhook.discord.DiscordEmbed
import com.example.demo.infrastructure.webhook.discord.DiscordFactoryProvider
import com.example.demo.infrastructure.webhook.discord.DiscordMessage
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifySequence
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class DiscordFactoryProviderTests :
	FunSpec({
		val mockWebClient = mockk<WebClient>()
		val requestBodyUriSpec = mockk<WebClient.RequestBodyUriSpec>()
		val requestBodySpec = mockk<WebClient.RequestBodySpec>()
		val responseSpec = mockk<WebClient.ResponseSpec>()

		val provider = DiscordFactoryProvider("https://discord.test/webhook", mockWebClient)

		val messages =
			listOf(
				DiscordMessage.of(
					title = "Deploy complete",
					messages = listOf("Service A deployed"),
					embeds =
						listOf(
							DiscordEmbed.of(
								title = "Service A deployed",
								description = "Deployment completed successfully",
								color = 0x00FF00
							)
						)
				)
			)

		test("send should post to the correct URL") {
			val slot = slot<Map<String, Any>>()

			every { mockWebClient.post() } returns requestBodyUriSpec
			every { requestBodyUriSpec.uri("https://discord.test/webhook") } returns requestBodySpec
			every { requestBodySpec.bodyValue(capture(slot)) } returns requestBodySpec
			every { requestBodySpec.retrieve() } returns responseSpec
			every { responseSpec.bodyToMono(String::class.java) } returns Mono.just("ok")

			provider.send(messages)

			slot.captured.shouldContainKey("content")

			verifySequence {
				mockWebClient.post()
				requestBodyUriSpec.uri("https://discord.test/webhook")
				requestBodySpec.bodyValue(any())
				requestBodySpec.retrieve()
				responseSpec.bodyToMono(String::class.java)
			}
		}

		test("generatePayload() should create valid Payload with formatted text and emojis") {
			fun resolveTitleEmoji(title: String): String =
				when {
					title.contains("error", ignoreCase = true) ||
						title.contains("fail", ignoreCase = true) -> "❌"

					title.contains("deploy", ignoreCase = true) ||
						title.contains("release", ignoreCase = true) -> "🚀"

					title.contains("warn", ignoreCase = true) -> "⚠️"
					else -> "📝"
				}

			fun resolveLineEmoji(line: String): String =
				when {
					line.contains("success", ignoreCase = true) ||
						line.contains("completed", ignoreCase = true) -> "✅"

					line.contains("error", ignoreCase = true) ||
						line.contains("fail", ignoreCase = true) -> "❌"

					line.contains("warn", ignoreCase = true) ||
						line.contains("slow", ignoreCase = true) -> "⚠️"

					else -> "🔹"
				}

			fun formatMessage(message: DiscordMessage): String {
				val titleEmoji = resolveTitleEmoji(message.title)
				val header = "$titleEmoji **[${message.title}]**"

				val body =
					message.messages.joinToString("\n") { line ->
						"${resolveLineEmoji(line)} $line"
					}

				return "$header\n$body"
			}

			fun generatePayload(messages: List<DiscordMessage>): Map<String, Any> {
				val content = messages.joinToString("\n\n", transform = ::formatMessage)
				val embeds = messages.mapNotNull { it.embeds }.flatten()

				return buildMap {
					put("content", content)
					if (embeds.isNotEmpty()) put("embeds", embeds)
				}
			}

			val payload = generatePayload(messages)

			payload["content"] shouldBe
				"""
				🚀 **[Deploy complete]**
				🔹 Service A deployed
				""".trimIndent()

			payload["embeds"] shouldBe
				listOf(
					DiscordEmbed.of(
						title = "Service A deployed",
						description = "Deployment completed successfully",
						color = 0x00FF00
					)
				)
		}
	})
