package com.example.demo.kotest.utils

import com.example.demo.common.dto.SlackMessage
import com.example.demo.utils.SlackUtils
import com.slack.api.webhook.Payload
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.test.context.ActiveProfiles
import java.lang.reflect.Method

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class SlackUtilsTests :
	StringSpec({

		lateinit var slackUtils: SlackUtils
		lateinit var generatePayloadMethod: Method

		beforeTest {
			slackUtils = SlackUtils()
			generatePayloadMethod =
				slackUtils::class.java
					.getDeclaredMethod("generatePayload", List::class.java)
					.apply { isAccessible = true }
		}

		"Should generate correct payload structure" {
			val dto = SlackMessage.of("Test Title", listOf("Message 1", "Message 2"))
			val payload = generatePayloadMethod.invoke(slackUtils, listOf(dto)) as Payload

			payload.text shouldBe "*Test Title*\n• Message 1\n• Message 2"
			payload.blocks.size shouldBe 4
		}
	})
