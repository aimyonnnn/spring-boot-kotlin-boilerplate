package com.example.demo.infrastructure.webhook.discord

data class DiscordMessage(
	val title: String,
	val messages: List<String>,
	val embeds: List<DiscordEmbed>? = null
) {
	companion object {
		fun of(
			title: String,
			messages: List<String>,
			embeds: List<DiscordEmbed>? = null
		): DiscordMessage {
			val exampleEmbed =
				DiscordEmbed.of(
					title = "Embed Title",
					description = "This is a description of the embed message.",
					color = 0x3498db,
					fields =
						mutableListOf(
							DiscordEmbedField.of(name = "Field1", value = "Value1", inline = true),
							DiscordEmbedField.of(name = "Field2", value = "Value2", inline = false)
						),
					footer = DiscordEmbedFooter.of(text = "Footer text")
				)

			return DiscordMessage(title, messages, embeds ?: mutableListOf(exampleEmbed))
		}
	}
}

data class DiscordEmbed(
	val title: String? = null,
	val description: String? = null,
	val color: Int? = null,
	val fields: List<DiscordEmbedField>? = null,
	val footer: DiscordEmbedFooter? = null
) {
	companion object {
		fun of(
			title: String? = null,
			description: String? = null,
			color: Int? = null,
			fields: List<DiscordEmbedField>? = null,
			footer: DiscordEmbedFooter? = null
		): DiscordEmbed = DiscordEmbed(title, description, color, fields, footer)
	}
}

data class DiscordEmbedField(
	val name: String,
	val value: String,
	val inline: Boolean = false
) {
	companion object {
		fun of(
			name: String,
			value: String,
			inline: Boolean = false
		): DiscordEmbedField = DiscordEmbedField(name, value, inline)
	}
}

data class DiscordEmbedFooter(
	val text: String
) {
	companion object {
		fun of(text: String): DiscordEmbedFooter = DiscordEmbedFooter(text)
	}
}
