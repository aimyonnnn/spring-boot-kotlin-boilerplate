package com.example.demo.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {
	@Bean
	fun corsConfigurationSource(): CorsConfigurationSource =
		UrlBasedCorsConfigurationSource().apply {
			registerCorsConfiguration(
				"/**",
				CorsConfiguration().apply {
					allowedOrigins = mutableListOf("*")
					allowedMethods = mutableListOf("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH")
					allowedHeaders = mutableListOf("Authorization", "Cache-Control", "Content-Type")
					allowCredentials = true
				}
			)
		}
}
