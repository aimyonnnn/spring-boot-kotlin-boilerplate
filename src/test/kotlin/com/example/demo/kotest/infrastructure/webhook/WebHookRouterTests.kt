package com.example.demo.kotest.infrastructure.webhook

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.WebHookRouter
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.slack.SlackWebHookSender
import io.kotest.assertions.throwables.shouldThrow
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
		val router = WebHookRouter(slackSender)

		context("route") {

			test("should return slackSender when target is SLACK") {
				val result = router.route(WebHookTarget.SLACK)
			
				result shouldBe slackSender
			}

			test("should throw CustomRuntimeException when target is DISCORD") {
				val exception =
					shouldThrow<CustomRuntimeException> {
						router.route(WebHookTarget.DISCORD)
					}

				exception.message shouldBe "The target is not implemented yet: DISCORD"
			}
		}

		context("all") {

			test("should return all supported senders") {
				val result = router.all()
				result shouldContainExactly listOf(slackSender)
			}
		}
	})
