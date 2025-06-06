package com.example.demo.common.config

import com.example.demo.common.exception.handler.WebClientExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
	private val webClientExceptionHandler: WebClientExceptionHandler
) {
	@Bean
	fun webClient(): WebClient =
		WebClient
			.builder()
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.filter(webClientExceptionHandler.errorHandlingFilter())
			.build()
}
