package com.example.demo.mockito.infrastructure.webhook.discord

import com.example.demo.infrastructure.webhook.common.CommonWebHookMessage
import com.example.demo.infrastructure.webhook.discord.DiscordEmbed
import com.example.demo.infrastructure.webhook.discord.DiscordMessage
import com.example.demo.infrastructure.webhook.discord.DiscordMessageConverter
import com.example.demo.infrastructure.webhook.discord.DiscordWebHookMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("mockito-unit-test")
@DisplayName("Mockito Unit - Discord Message Converter Test")
@ExtendWith(
	MockitoExtension::class
)
class DiscordMessageConverterTests {
	private val converter = DiscordMessageConverter()

	@Test
	fun `convertCommonToDiscordMessage should convert CommonWebHookMessage to DiscordWebHookMessage`() {
		val commonMessage =
			CommonWebHookMessage(
				title = "Test Title",
				contents = listOf("Line 1", "Line 2")
			)

		val result = converter.convertCommonToDiscordMessage(commonMessage)

		assertEquals(1, result.messages.size)
		val message = result.messages[0]
		assertEquals("Test Title", message.title)
		assertEquals(listOf("Line 1", "Line 2"), message.messages)
	}

	@Test
	fun `convertToDiscordMessage should return the same DiscordWebHookMessage instance`() {
		val discordMessage =
			DiscordWebHookMessage(
				mutableListOf(
					DiscordMessage(
						"Alert",
						mutableListOf("msg1", "msg2"),
						mutableListOf(
							DiscordEmbed.of(
								title = "Service A deployed",
								description = "Deployment completed successfully",
								color = 0x00FF00
							)
						)
					)
				)
			)

		val result = converter.convertToDiscordMessage(discordMessage)

		assertSame(discordMessage, result)
	}
}
