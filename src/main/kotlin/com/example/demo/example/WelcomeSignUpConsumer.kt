package com.example.demo.example

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.kafka.provider.KafkaTopicMetaProvider
import com.example.demo.infrastructure.mail.MailHelper
import com.example.demo.infrastructure.mail.MailPayload
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class WelcomeSignUpConsumer(
	private val mailHelper: MailHelper
) {
	@KafkaListener(
		topics = [KafkaTopicMetaProvider.MAIL_TOPIC],
		groupId = KafkaTopicMetaProvider.MAIL_GROUP,
		containerFactory = KafkaTopicMetaProvider.MAIL_CONTAINER_FACTORY
	)
	@Retryable(
		maxAttempts = 3,
		backoff = Backoff(delay = 2000)
	)
	fun consume(payload: MailPayload) {
		try {
			mailHelper.sendEmail(payload)
		} catch (exception: CustomRuntimeException) {
			logger.error { "Failed to send email: ${exception.message}" }

			throw exception
		}
	}
}
