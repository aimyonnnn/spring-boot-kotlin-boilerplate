package com.example.demo.kotest.infrastructure.webhook.slack

import com.example.demo.infrastructure.webhook.common.CommonWebHookMessage
import com.example.demo.infrastructure.webhook.slack.SlackMessage
import com.example.demo.infrastructure.webhook.slack.SlackMessageConverter
import com.example.demo.infrastructure.webhook.slack.SlackWebHookMessage
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class SlackMessageConverterTests :
	FunSpec({
		val converter = SlackMessageConverter()

		test("convertCommonToSlackMessage should convert CommonWebHookMessage to SlackWebHookMessage") {
			val commonMessage =
				CommonWebHookMessage(
					title = "Test Title",
					contents = listOf("Line 1", "Line 2")
				)

			val result = converter.convertCommonToSlackMessage(commonMessage)

			result.messages shouldHaveSize 1
			result.messages[0].title shouldBe "Test Title"
			result.messages[0].messages shouldContainExactly listOf("Line 1", "Line 2")
		}

		test("convertToSlackMessage should return the same SlackWebHookMessage instance") {
			val slackMessage =
				SlackWebHookMessage(
					mutableListOf(SlackMessage("Alert", mutableListOf("msg1", "msg2")))
				)

			val result = converter.convertToSlackMessage(slackMessage)

			result shouldBe slackMessage
		}
	})
