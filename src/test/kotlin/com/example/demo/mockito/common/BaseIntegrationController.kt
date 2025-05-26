package com.example.demo.mockito.common

import com.example.demo.common.aop.SendWebHookSignalRequestBodyAdvice
import com.example.demo.infrastructure.webhook.WebHookProvider
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext

abstract class BaseIntegrationController {
	@Autowired
	protected lateinit var webApplicationContext: WebApplicationContext

	@Autowired
	protected lateinit var objectMapper: ObjectMapper

	@Suppress("Unused")
	@Autowired
	protected lateinit var sendWebHookSignalRequestBodyAdvice: SendWebHookSignalRequestBodyAdvice

	@Suppress("Unused")
	@MockitoBean
	protected lateinit var webHookProvider: WebHookProvider

	protected lateinit var mockMvc: MockMvc

	/**
	 * ResponseAdvice Status
	 */
	protected val commonStatus: Int = HttpStatus.OK.value()

	/**
	 * ResponseAdvice Message
	 */
	protected val commonMessage: String = HttpStatus.OK.name
}
