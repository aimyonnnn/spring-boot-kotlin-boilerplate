package com.example.demo.mockito.infrastructure.webhook

import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.infrastructure.webhook.WebHookRouter
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.slack.SlackWebHookSender
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("mockito-unit-test")
@DisplayName("Mockito Unit - WebHook Router Test")
@ExtendWith(
	MockitoExtension::class
)
class WebHookRouterTests {
	@Mock
	private lateinit var slackSender: SlackWebHookSender
	private lateinit var router: WebHookRouter

	@BeforeEach
	fun setUp() {
		MockitoAnnotations.openMocks(this)
		router = WebHookRouter(slackSender)
	}

	@Test
	fun `should return slackSender when target is SLACK`() {
		val result = router.route(WebHookTarget.SLACK)
		assertEquals(slackSender, result)
	}

	@Test
	fun `should throw CustomRuntimeException when target is DISCORD`() {
		val exception =
			assertThrows<CustomRuntimeException> {
				router.route(WebHookTarget.DISCORD)
			}

		assertEquals("The target is not implemented yet: DISCORD", exception.message)
	}

	@Test
	fun `should return all supported senders`() {
		val result = router.all()
		assertEquals(listOf(slackSender), result)
	}
}
