package com.example.demo.kotest.infrastructure.webhook

import com.example.demo.infrastructure.webhook.WebHookRouter
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.discord.DiscordWebHookSender
import com.example.demo.infrastructure.webhook.slack.SlackWebHookSender
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class WebHookRouterTests :
	FunSpec({
		val slackSender = mockk<SlackWebHookSender>()
		val discordSender = mockk<DiscordWebHookSender>()
		val router = WebHookRouter(slackSender, discordSender)

		context("route") {

			test("should return slackSender when target is SLACK") {
				val result = router.route(WebHookTarget.SLACK)

				result shouldBe slackSender
			}

			test("should return discordSender when target is DISCORD") {
				val result = router.route(WebHookTarget.DISCORD)

				result shouldBe discordSender
			}
		}

		context("all") {

			test("should return all supported senders") {
				val result = router.all()
				result shouldContainExactly listOf(slackSender, discordSender)
			}
		}
	})
