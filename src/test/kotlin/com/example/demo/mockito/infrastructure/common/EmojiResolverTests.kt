package com.example.demo.mockito.infrastructure.common

import com.example.demo.infrastructure.webhook.common.EmojiResolver
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tag("mockito-unit-test")
@DisplayName("Mockito Unit - Emoji Resolver Test")
@ExtendWith(
	MockitoExtension::class
)
class EmojiResolverTests {
	@Nested
	@DisplayName("resolveTitleEmoji tests")
	inner class ResolveTitleEmojiTests {
		@ParameterizedTest(name = "should return {1} for title: \"{0}\"")
		@CsvSource(
			"An error occurred, ❌",
			"Build failed!, ❌",
			"FAILING pipeline, ❌",
			"Deploying to production, 🚀",
			"Release 1.0.0 completed, 🚀",
			"Warning: low disk space, ⚠️",
			"Generic update message, 📝"
		)
		fun testResolveTitleEmoji(
			input: String,
			expected: String
		) {
			assertEquals(expected, EmojiResolver.resolveTitleEmoji(input))
		}
	}

	@Nested
	@DisplayName("resolveLineEmoji tests")
	inner class ResolveLineEmojiTests {
		@ParameterizedTest(name = "should return {1} for line: \"{0}\"")
		@CsvSource(
			"Build completed successfully, ✅",
			"SUCCESS: All tests passed, ✅",
			"Runtime error at line 32, ❌",
			"FAIL: service did not start, ❌",
			"WARNING: something looks off, ⚠️",
			"Response is slow, ⚠️",
			"Checking something neutral, 🔹"
		)
		fun testResolveLineEmoji(
			input: String,
			expected: String
		) {
			assertEquals(expected, EmojiResolver.resolveLineEmoji(input))
		}
	}
}
