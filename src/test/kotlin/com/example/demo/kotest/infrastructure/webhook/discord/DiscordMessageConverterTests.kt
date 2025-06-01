package com.example.demo.kotest.infrastructure.webhook.discord

import com.example.demo.infrastructure.webhook.common.CommonWebHookMessage
import com.example.demo.infrastructure.webhook.discord.DiscordEmbed
import com.example.demo.infrastructure.webhook.discord.DiscordMessage
import com.example.demo.infrastructure.webhook.discord.DiscordMessageConverter
import com.example.demo.infrastructure.webhook.discord.DiscordWebHookMessage
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class DiscordMessageConverterTests :
	FunSpec({
		val converter = DiscordMessageConverter()

		test("convertCommonToDiscordMessage should convert CommonWebHookMessage to DiscordWebHookMessage") {
			val commonMessage =
				CommonWebHookMessage(
					title = "Test Title",
					contents = listOf("Line 1", "Line 2")
				)

			val result = converter.convertCommonToDiscordMessage(commonMessage)

			result.messages shouldHaveSize 1
			result.messages[0].title shouldBe "Test Title"
			result.messages[0].messages shouldContainExactly listOf("Line 1", "Line 2")
		}

		test("convertToDiscordMessage should return the same DiscordWebHookMessage instance") {
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

			result shouldBe discordMessage
		}
	})
