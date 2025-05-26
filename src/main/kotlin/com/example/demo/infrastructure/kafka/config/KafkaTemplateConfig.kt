package com.example.demo.infrastructure.kafka.config

import com.example.demo.infrastructure.kafka.provider.KafkaProducerFactoryProvider
import com.example.demo.infrastructure.mail.MailPayload
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.KafkaTemplate

@Configuration
@EnableKafka
class KafkaTemplateConfig(
	private val factoryProvider: KafkaProducerFactoryProvider
) {
	@Bean
	fun mailKafkaTemplate(): KafkaTemplate<String, MailPayload> = factoryProvider.createKafkaTemplate(MailPayload::class.java)

	@Bean
	fun defaultKafkaTemplate(): KafkaTemplate<String, Any> = factoryProvider.createKafkaTemplate(Any::class.java)
}
