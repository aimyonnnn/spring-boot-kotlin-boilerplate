package com.example.demo.infrastructure.kafka.provider

import com.example.demo.infrastructure.kafka.DlqHelper
import com.example.demo.infrastructure.webhook.WebHookProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class KafkaConsumerFactoryProvider(
	private val webHookProvider: WebHookProvider,
	private val kafkaTemplate: KafkaTemplate<String, Any>
) {
	fun <T : Any> createFactory(valueType: Class<T>): ConcurrentKafkaListenerContainerFactory<String, T> {
		val config =
			mapOf(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
				ConsumerConfig.GROUP_ID_CONFIG to valueType.simpleName.lowercase() + "-group",
				ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
				ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to ErrorHandlingDeserializer::class.java,
				ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ErrorHandlingDeserializer::class.java,
				ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS to StringDeserializer::class.java.name,
				ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS to JsonDeserializer::class.java.name,
				JsonDeserializer.TRUSTED_PACKAGES to "*",
				JsonDeserializer.VALUE_DEFAULT_TYPE to valueType.name
			)

		val consumerFactory: ConsumerFactory<String, T> =
			DefaultKafkaConsumerFactory(config)

		return ConcurrentKafkaListenerContainerFactory<String, T>().apply {
			setConsumerFactory(consumerFactory)
			setCommonErrorHandler(createErrorHandler())
		}
	}

	private fun createErrorHandler(): DefaultErrorHandler {
		val recover =
			DeadLetterPublishingRecoverer(kafkaTemplate) { record, exception ->
				logger.error(exception) { "Kafka Consumer Error (after retries): ${exception.message}" }
				logger.error { "Failed Record: $record" }

				webHookProvider.sendAll("❗ Kafka Consumer Error", mutableListOf("Error: ${exception.message}", "Failed Record: $record"))

				DlqHelper.resolveDlqTopicPartition(
					kafkaTemplate,
					record.topic(),
					record.partition()
				)
			}

		return DefaultErrorHandler(recover)
	}
}
