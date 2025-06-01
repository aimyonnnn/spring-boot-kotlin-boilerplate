package com.example.demo.kotest.infrastructure.webhook.discord

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.common.WebHookMessage
import com.example.demo.infrastructure.webhook.discord.DiscordFactoryProvider
import com.example.demo.infrastructure.webhook.discord.DiscordMessage
import com.example.demo.infrastructure.webhook.discord.DiscordWebHookMessage
import com.example.demo.infrastructure.webhook.discord.DiscordWebHookSender
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class DiscordWebHookSenderTests :
	StringSpec({

		val discordFactoryProvider = mockk<DiscordFactoryProvider>()
		val sender = DiscordWebHookSender(discordFactoryProvider)

		"should return DISCORD as target" {
			sender.target().name shouldBe "DISCORD"
		}

		"should send DiscordWebHookMessage to DiscordFactoryProvider" {
			val discordMessage =
				DiscordWebHookMessage(
					mutableListOf(
						DiscordMessage("Test Title", mutableListOf("msg1", "msg2"))
					)
				)

			every { discordFactoryProvider.send(any<List<DiscordMessage>>()) } just Runs

			sender.send(discordMessage)

			verify(exactly = 1) { discordFactoryProvider.send(discordMessage.messages) }
		}

		"should throw CustomRuntimeException when message is not DiscordWebHookMessage" {
			val invalidMessage = object : WebHookMessage {}

			val exception =
				shouldThrow<CustomRuntimeException> {
					sender.send(invalidMessage)
				}

			exception.message shouldBe "Invalid message type for Discord"
		}
	})
