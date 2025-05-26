package com.example.demo.mockito.infrastructure.mail

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.mail.MailHelper
import com.example.demo.infrastructure.mail.MailPayload
import com.example.demo.infrastructure.webhook.WebHookProvider
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("mockito-unit-test")
@DisplayName("Mockito Unit - Mail Helper Test")
@ExtendWith(
	MockitoExtension::class
)
class MailHelperTests {
	private lateinit var mailSender: MailSender
	private lateinit var webHookProvider: WebHookProvider
	private lateinit var mailHelper: MailHelper

	@BeforeEach
	fun setup() {
		mailSender = mock(MailSender::class.java)
		webHookProvider = mock(WebHookProvider::class.java)
		mailHelper = MailHelper(mailSender, webHookProvider)
	}

	@Test
	fun `should send email with correct values`() {
		val payload =
			MailPayload.of(
				to = "awakelife93@gmail.com",
				subject = "Test Subject",
				body = "Test Body"
			)

		mailHelper.sendEmail(payload)

		val messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage::class.java)
		verify(mailSender, times(1)).send(messageCaptor.capture())

		val captured = messageCaptor.value
		assertArrayEquals(arrayOf("awakelife93@gmail.com"), captured.to)
		assertEquals("Test Subject", captured.subject)
		assertEquals("Test Body", captured.text)
	}

	@Test
	fun `should throw exception for invalid email`() {
		val payload =
			MailPayload.of(
				to = "invalid-email",
				subject = "Subject",
				body = "Body"
			)

		val exception =
			assertThrows<CustomRuntimeException> {
				mailHelper.sendEmail(payload)
			}

		assertEquals(
			"Mail sending failed: Validation failed: to must be a valid email",
			exception.message
		)

		verify(mailSender, times(0)).send(any(SimpleMailMessage::class.java))
	}

	@Test
	fun `should call sendSlack on mail sending failure`() {
		val payload =
			MailPayload.of(
				to = "awakelife93@gmail.com",
				subject = "Test Subject",
				body = "Test Body"
			)

		doThrow(CustomRuntimeException("SMTP error"))
			.`when`(mailSender)
			.send(any(SimpleMailMessage::class.java))

		doNothing().`when`(webHookProvider).sendSlack(anyString(), anyList())

		assertThrows<CustomRuntimeException> {
			mailHelper.sendEmail(payload)
		}

		verify(webHookProvider, times(1)).sendSlack(
			eq("Mail Sending Failed"),
			eq(
				listOf(
					"Mail to: ${payload.to}",
					"Mail Subject: ${payload.subject}",
					"Mail Body: ${payload.body}",
					"Error: SMTP error"
				)
			)
		)
	}
}
