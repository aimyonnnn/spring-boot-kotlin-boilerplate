package com.example.demo.infrastructure.webhook.slack

import com.example.demo.infrastructure.webhook.common.EmojiResolver.resolveLineEmoji
import com.example.demo.infrastructure.webhook.common.EmojiResolver.resolveTitleEmoji
import com.slack.api.Slack
import com.slack.api.model.block.DividerBlock
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.webhook.Payload
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class SlackFactoryProvider(
	@Value("\${webhook.slack.url}") private val url: String
) {
	private val slackClient: Slack = Slack.getInstance()

	@Async("webhookExecutor")
	fun send(slackMessage: List<SlackMessage>) {
		runCatching {
			slackClient.send(url, generatePayload(slackMessage)).also {
				logger.info { "webhook (slack) async method started on thread: ${Thread.currentThread().name}" }

				require(it.code == 200) {
					"Slack response code: ${it.code}, message: ${it.message}"
				}
			}
		}.onFailure { exception ->
			logger.error(exception) { "Failed to send message to Slack" }
			throw exception
		}
	}

	private fun generatePayload(slackMessage: List<SlackMessage>): Payload {
		val blocks =
			slackMessage.flatMapIndexed { index, dto ->
				buildMessageBlock(dto, isLast = index == slackMessage.lastIndex)
			}

		val text =
			slackMessage.joinToString("\n\n") { dto ->
				"*${dto.title}*\n" + dto.messages.joinToString("\n") { "• $it" }
			}

		return Payload
			.builder()
			.text(text)
			.blocks(blocks)
			.build()
	}

	private fun buildMessageBlock(
		dto: SlackMessage,
		isLast: Boolean
	): List<LayoutBlock> =
		mutableListOf<LayoutBlock>().apply {
			add(sectionBlock(resolveTitleEmoji(dto.title) + " *${dto.title}*"))
			add(dividerBlock())
			addAll(dto.messages.map { sectionBlock(resolveLineEmoji(it) + " $it") })

			if (!isLast) add(dividerBlock())
		}

	private fun sectionBlock(text: String) =
		SectionBlock
			.builder()
			.text(markdownText(text))
			.build()

	private fun dividerBlock() = DividerBlock()

	private fun markdownText(text: String) =
		MarkdownTextObject
			.builder()
			.text(text)
			.build()
}
