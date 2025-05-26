package com.example.demo.kotest.infrastructure.webhook

import com.example.demo.infrastructure.webhook.WebHookMessageConverter
import com.example.demo.infrastructure.webhook.WebHookProvider
import com.example.demo.infrastructure.webhook.WebHookRouter
import com.example.demo.infrastructure.webhook.common.CommonWebHookMessage
import com.example.demo.infrastructure.webhook.common.WebHookMessage
import com.example.demo.infrastructure.webhook.common.WebHookSender
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.slack.SlackWebHookMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class WebHookProviderTests :
	FunSpec({
		val router = mockk<WebHookRouter>()
		val converter = mockk<WebHookMessageConverter>()
		val sender = mockk<WebHookSender>(relaxed = true)
		val slackTarget = WebHookTarget.SLACK
		val slackMessage = SlackWebHookMessage(mutableListOf())
		val commonMessage = CommonWebHookMessage("Title", listOf("Line1", "Line2"))

		context("webhook.enabled == true") {
			val provider = WebHookProvider(router, converter, true)

			test("sendSlack should convert and send a message for the SLACK target") {

				every { router.route(slackTarget) } returns sender

				every { converter.convert(slackTarget, any<WebHookMessage>()) } returns slackMessage

				provider.sendSlack("Title", listOf("Line1", "Line2"))

				verify {
					router.route(slackTarget)
					converter.convert(slackTarget, any<WebHookMessage>())
					sender.send(slackMessage)
				}
			}

			test("sendAll should convert the message for each sender and send it to all targets") {
				val sender1 = mockk<WebHookSender>(relaxed = true)
				val sender2 = mockk<WebHookSender>(relaxed = true)

				every { router.all() } returns listOf(sender1, sender2)

				every { sender1.target() } returns WebHookTarget.SLACK

				every { sender2.target() } returns WebHookTarget.SLACK

				every { converter.convert(any<WebHookTarget>(), any<WebHookMessage>()) } returns slackMessage

				provider.sendAll("Title", listOf("Line1", "Line2"))

				verify { converter.convert(WebHookTarget.SLACK, commonMessage) }

				verify { sender1.send(slackMessage) }
			
				verify { sender2.send(slackMessage) }
			}
		}

		context("webhook.enabled == false") {
			val providerDisabled = WebHookProvider(router, converter, false)

			test("An exception should be thrown if enabled is set to false") {

				val exception =
					shouldThrow<IllegalStateException> {
						providerDisabled.sendAll("Title", listOf("Line1"))
					}

				exception.message shouldBe "Webhook is not enabled"
			}
		}
	})
