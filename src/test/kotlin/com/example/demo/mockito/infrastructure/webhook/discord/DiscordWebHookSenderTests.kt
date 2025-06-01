package com.example.demo.mockito.infrastructure.webhook.discord

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.common.WebHookMessage
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.discord.DiscordEmbed
import com.example.demo.infrastructure.webhook.discord.DiscordFactoryProvider
import com.example.demo.infrastructure.webhook.discord.DiscordMessage
import com.example.demo.infrastructure.webhook.discord.DiscordWebHookMessage
import com.example.demo.infrastructure.webhook.discord.DiscordWebHookSender
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("mockito-unit-test")
@DisplayName("Mockito Unit - Discord WebHook Sender Test")
@ExtendWith(
	MockitoExtension::class
)
class DiscordWebHookSenderTests {
	@Mock
	private lateinit var discordFactoryProvider: DiscordFactoryProvider
	private lateinit var sender: DiscordWebHookSender

	@BeforeEach
	fun setup() {
		sender = DiscordWebHookSender(discordFactoryProvider)
	}

	@Test
	fun `should return DISCORD as target`() {
		val result = sender.target()
		assertEquals(WebHookTarget.DISCORD, result)
	}

	@Test
	fun `should send DiscordWebHookMessage to DiscordFactoryProvider`() {
		val message =
			DiscordMessage(
				"Test Title",
				mutableListOf("msg1", "msg2"),
				mutableListOf(
					DiscordEmbed.of(
						title = "Service A deployed",
						description = "Deployment completed successfully",
						color = 0x00FF00
					)
				)
			)
		val discordWebHookMessage = DiscordWebHookMessage(mutableListOf(message))

		sender.send(discordWebHookMessage)

		verify(discordFactoryProvider, times(1)).send(discordWebHookMessage.messages)
	}

	@Test
	fun `should throw exception when message is not DiscordWebHookMessage`() {
		val invalidMessage = object : WebHookMessage {}

		val exception =
			assertThrows(CustomRuntimeException::class.java) {
				sender.send(invalidMessage)
			}

		assertEquals("Invalid message type for Discord", exception.message)
		verify(discordFactoryProvider, never()).send(any<List<DiscordMessage>>())
	}
}
