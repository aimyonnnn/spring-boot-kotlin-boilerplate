package com.example.demo.common.exception.handler

import com.example.demo.common.exception.CustomRuntimeException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Component
class WebClientExceptionHandler {
	fun errorHandlingFilter(): ExchangeFilterFunction =
		ExchangeFilterFunction.ofResponseProcessor { response ->
			if (!response.statusCode().isError) return@ofResponseProcessor Mono.just(response)

			response.bodyToMono(String::class.java).flatMap { body ->
				val status = response.statusCode()
				val uri = response.request().uri
				val message = "API call to $uri failed with status $status. Response body: $body"

				logger.error { message }
				Mono.error(CustomRuntimeException(message))
			}
		}
}
