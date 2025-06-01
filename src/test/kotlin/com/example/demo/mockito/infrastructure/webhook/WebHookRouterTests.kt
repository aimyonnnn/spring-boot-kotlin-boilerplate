package com.example.demo.mockito.infrastructure.webhook

import com.example.demo.infrastructure.webhook.WebHookRouter
import com.example.demo.infrastructure.webhook.constant.WebHookTarget
import com.example.demo.infrastructure.webhook.discord.DiscordWebHookSender
import com.example.demo.infrastructure.webhook.slack.SlackWebHookSender
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
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

	@Mock
	private lateinit var discordSender: DiscordWebHookSender
	private lateinit var router: WebHookRouter

	@BeforeEach
	fun setUp() {
		MockitoAnnotations.openMocks(this)
		router = WebHookRouter(slackSender, discordSender)
	}

	@Test
	fun `should return slackSender when target is SLACK`() {
		val result = router.route(WebHookTarget.SLACK)
		assertEquals(slackSender, result)
	}

	@Test
	fun `should return discordSender when target is DISCORD`() {
		val result = router.route(WebHookTarget.DISCORD)
		assertEquals(discordSender, result)
	}

	@Test
	fun `should return all supported senders`() {
		val result = router.all()
		assertEquals(listOf(slackSender, discordSender), result)
	}
}
