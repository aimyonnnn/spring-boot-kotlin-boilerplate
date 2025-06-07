package com.example.demo.kotest.infrastructure.webhook.discord

import com.example.demo.infrastructure.webhook.discord.DiscordEmbed
import com.example.demo.infrastructure.webhook.discord.DiscordFactoryProvider
import com.example.demo.infrastructure.webhook.discord.DiscordMessage
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainKey
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
	})
