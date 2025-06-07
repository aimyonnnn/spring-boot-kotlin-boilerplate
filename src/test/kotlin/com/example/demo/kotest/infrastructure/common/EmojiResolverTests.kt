package com.example.demo.kotest.infrastructure.common

import com.example.demo.infrastructure.webhook.common.EmojiResolver
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class EmojiResolverTests :
	StringSpec({
		"resolveTitleEmoji should return correct emoji based on title content" {
			forAll(
				row("An error occurred", "❌", "error"),
				row("Build failed!", "❌", "fail"),
				row("FAILING pipeline", "❌", "fail"),
				row("Deploying to production", "🚀", "deploy"),
				row("Release 1.0.0 completed", "🚀", "release"),
				row("Warning: low disk space", "⚠️", "warn"),
				row("Generic update message", "📝", "default")
			) { title: String, expectedEmoji: String, _ ->
				EmojiResolver.resolveTitleEmoji(title) shouldBe expectedEmoji
			}
		}

		"resolveLineEmoji should return correct emoji based on line content" {
			forAll(
				row("Build completed successfully", "✅", "completed"),
				row("SUCCESS: All tests passed", "✅", "success"),
				row("Runtime error at line 32", "❌", "error"),
				row("FAIL: service did not start", "❌", "fail"),
				row("WARNING: something looks off", "⚠️", "warn"),
				row("Response is slow", "⚠️", "slow"),
				row("Checking something neutral", "🔹", "default")
			) { line: String, expectedEmoji: String, _ ->
				EmojiResolver.resolveLineEmoji(line) shouldBe expectedEmoji
			}
		}
	})
