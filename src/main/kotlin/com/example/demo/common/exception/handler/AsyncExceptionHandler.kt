package com.example.demo.common.exception.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import java.lang.reflect.Method

private val logger = KotlinLogging.logger {}

class AsyncExceptionHandler : AsyncUncaughtExceptionHandler {
	override fun handleUncaughtException(
		exception: Throwable,
		method: Method,
		vararg params: Any?
	) {
		logger.error(exception) {
			"Async exception occurred in method: ${method.name}, with params: ${params.joinToString()}"
		}
	}
}
