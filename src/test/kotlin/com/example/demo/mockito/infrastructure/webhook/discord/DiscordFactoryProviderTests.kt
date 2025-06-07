package com.example.demo.mockito.infrastructure.webhook.discord

import com.example.demo.infrastructure.webhook.discord.DiscordEmbed
import com.example.demo.infrastructure.webhook.discord.DiscordFactoryProvider
import com.example.demo.infrastructure.webhook.discord.DiscordMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@ActiveProfiles("test")
@Tag("mockito-unit-test")
@DisplayName("Mockito Unit - Discord Factory Provider Test")
@ExtendWith(
	MockitoExtension::class
)
class DiscordFactoryProviderTests {
	@Mock
	private lateinit var webClient: WebClient

	@Mock
	private lateinit var requestBodyUriSpec: WebClient.RequestBodyUriSpec

	@Mock
	private lateinit var requestBodySpec: WebClient.RequestBodySpec

	@Mock
	private lateinit var responseSpec: WebClient.ResponseSpec

	private lateinit var provider: DiscordFactoryProvider

	@BeforeEach
	fun setUp() {
		provider = DiscordFactoryProvider("https://discord.test/webhook", webClient)
	}

	@Test
	fun `send should post to the correct URL`() {
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

		val payloadCaptor = argumentCaptor<Map<String, Any>>()

		whenever(webClient.post()).thenReturn(requestBodyUriSpec)
		whenever(requestBodyUriSpec.uri("https://discord.test/webhook")).thenReturn(requestBodySpec)
		whenever(requestBodySpec.bodyValue(payloadCaptor.capture())).thenReturn(requestBodySpec)
		whenever(requestBodySpec.retrieve()).thenReturn(responseSpec)
		whenever(responseSpec.bodyToMono(String::class.java)).thenReturn(Mono.just("ok"))

		provider.send(messages)

		verify(webClient).post()
		verify(requestBodyUriSpec).uri("https://discord.test/webhook")
		verify(requestBodySpec).bodyValue(any())
		verify(requestBodySpec).retrieve()
		verify(responseSpec).bodyToMono(String::class.java)

		val captured = payloadCaptor.firstValue
		assertThat(captured).containsKey("content")
		assertThat(captured["content"]).isEqualTo(
			"""
			🚀 **[Deploy complete]**
			🚀 Service A deployed
			""".trimIndent()
		)

		assertThat(captured["embeds"]).isEqualTo(
			listOf(
				DiscordEmbed.of(
					title = "Service A deployed",
					description = "Deployment completed successfully",
					color = 0x00FF00
				)
			)
		)
	}
}
