package com.example.demo.infrastructure.webhook.constant

enum class EmojiKeyword(
	val keywords: List<String>,
	val emoji: String
) {
	ERROR(listOf("error", "fail"), "❌"),
	DEPLOY(listOf("deploy", "release"), "🚀"),
	WARN(listOf("warn"), "⚠️"),
	DEFAULT_TITLE(emptyList(), "📝"),

	SUCCESS(listOf("success", "completed"), "✅"),
	FAIL(listOf("error", "fail"), "❌"),
	SLOW(listOf("warn", "slow"), "⚠️"),
	DEFAULT_LINE(emptyList(), "🔹")
}
