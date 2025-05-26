package com.example.demo.infrastructure.mail

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.WebHookProvider
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Component

@Component
class MailHelper(
	private val mailSender: MailSender,
	private val webHookProvider: WebHookProvider
) {
	fun sendEmail(mailPayload: MailPayload) {
		runCatching { sendMail(mailPayload) }
			.onFailure {
				handleMailFailure(mailPayload, it)
				throw CustomRuntimeException("Mail sending failed: ${it.message}")
			}
	}

	private fun sendMail(mailPayload: MailPayload) {
		validateMailPayload(mailPayload)

		val message = createMailMessage(mailPayload)
		mailSender.send(message)
	}

	private fun validateMailPayload(mailPayload: MailPayload) {
		val errors = mutableListOf<String>()

		validateTo(mailPayload.to, errors)

		validateSubject(mailPayload.subject, errors)

		validateBody(mailPayload.body, errors)

		if (errors.isNotEmpty()) {
			throw CustomRuntimeException("Validation failed: ${errors.joinToString(", ")}")
		}
	}

	private fun createMailMessage(mailPayload: MailPayload): SimpleMailMessage =
		SimpleMailMessage().apply {
			setTo(mailPayload.to)
			subject = mailPayload.subject
			text = mailPayload.body
		}

	private fun handleMailFailure(
		mailPayload: MailPayload,
		exception: Throwable
	) {
		webHookProvider.sendSlack(
			"Mail Sending Failed",
			mutableListOf(
				"Mail to: ${mailPayload.to}",
				"Mail Subject: ${mailPayload.subject}",
				"Mail Body: ${mailPayload.body}",
				"Error: ${exception.message}"
			)
		)
	}

	private fun validateTo(
		to: String,
		errors: MutableList<String>
	) {
		if (to.isBlank()) {
			errors.add("to cannot be empty")
		} else if (!Regex("^[A-Za-z0-9+_.-]+@(.+)$").matches(to)) {
			errors.add("to must be a valid email")
		}
	}

	private fun validateSubject(
		subject: String,
		errors: MutableList<String>
	) {
		if (subject.isBlank()) {
			errors.add("Subject cannot be empty")
		}
	}

	private fun validateBody(
		body: String,
		errors: MutableList<String>
	) {
		if (body.isBlank()) {
			errors.add("Body cannot be empty")
		}
	}
}
