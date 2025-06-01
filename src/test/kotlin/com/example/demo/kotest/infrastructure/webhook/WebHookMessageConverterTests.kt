package com.example.demo.kotest.infrastructure.webhook

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.WebHookMessageConverter
import com.example.demo.infrastructure.webhook.common.CommonWebHookMessage
import com.example.demo.infrastructure.webhook.common.WebHookMessage
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.discord.DiscordMessage
import com.example.demo.infrastructure.webhook.discord.DiscordMessageConverter
import com.example.demo.infrastructure.webhook.discord.DiscordWebHookMessage
import com.example.demo.infrastructure.webhook.slack.SlackMessage
import com.example.demo.infrastructure.webhook.slack.SlackMessageConverter
import com.example.demo.infrastructure.webhook.slack.SlackWebHookMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import io.mockk.every
import io.mockk.mockk
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class WebHookMessageConverterTests :
	FunSpec({
		val slackConverter = mockk<SlackMessageConverter>()
		val discordConverter = mockk<DiscordMessageConverter>()
		val converter = WebHookMessageConverter(slackConverter, discordConverter)

		val slackMessage = SlackWebHookMessage(mutableListOf(SlackMessage("line1", mutableListOf("line2"))))
		val convertedSlackMessage = SlackWebHookMessage(mutableListOf(SlackMessage("converted line", mutableListOf("converted line2"))))

		val discordMessage = DiscordWebHookMessage(mutableListOf(DiscordMessage("line1", mutableListOf("line2"))))
		val convertedDiscordMessage = DiscordWebHookMessage(mutableListOf(DiscordMessage("converted line", mutableListOf("converted line2"))))

		val commonMessage = CommonWebHookMessage("title", mutableListOf("line1", "line2"))

		context("convert()") {

			test("should convert SlackWebHookMessage when target is SLACK") {
				every { slackConverter.convertToSlackMessage(slackMessage) } returns convertedSlackMessage

				val result = converter.convert(WebHookTarget.SLACK, slackMessage)

				result shouldBe convertedSlackMessage
			}

			test("should convert DiscordWebHookMessage when target is DISCORD") {
				every { discordConverter.convertToDiscordMessage(discordMessage) } returns convertedDiscordMessage

				val result = converter.convert(WebHookTarget.DISCORD, discordMessage)

				result shouldBe convertedDiscordMessage
			}

			test("should convert CommonWebHookMessage to Slack message when target is SLACK") {
				every { slackConverter.convertCommonToSlackMessage(commonMessage) } returns convertedSlackMessage

				val result = converter.convert(WebHookTarget.SLACK, commonMessage)

				result shouldBe convertedSlackMessage
			}

			test("should convert CommonWebHookMessage to Discord message when target is Discord") {
				every { discordConverter.convertCommonToDiscordMessage(commonMessage) } returns convertedDiscordMessage

				val result = converter.convert(WebHookTarget.DISCORD, commonMessage)

				result shouldBe convertedDiscordMessage
			}

			test("should throw exception when unknown message type is passed for SLACK") {
				val unknownMessage = object : WebHookMessage {}

				val exception =
					shouldThrow<CustomRuntimeException> {
						converter.convert(WebHookTarget.SLACK, unknownMessage)
					}

				exception.message shouldStartWith "Unsupported message type: class"
				exception.message shouldEndWith ("for Slack")
			}

			test("should throw exception when unknown message type is passed for DISCORD") {
				val unknownMessage = object : WebHookMessage {}

				val exception =
					shouldThrow<CustomRuntimeException> {
						converter.convert(WebHookTarget.DISCORD, unknownMessage)
					}

				exception.message shouldStartWith "Unsupported message type: class"
				exception.message shouldEndWith ("for Discord")
			}
		}
	})
