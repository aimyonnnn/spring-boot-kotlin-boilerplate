package com.example.demo.common.aop

import com.example.demo.common.annotaction.SendSlackSignalRequest
import com.example.demo.common.dto.SlackMessage
import com.example.demo.utils.SlackUtils
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter
import java.lang.reflect.Type

@ControllerAdvice
class SendSlackSignalRequestBodyAdvice(
	private val slackUtils: SlackUtils
) : RequestBodyAdviceAdapter() {
	override fun supports(
		methodParameter: MethodParameter,
		targetType: Type,
		converterType: Class<out HttpMessageConverter<*>>
	): Boolean = methodParameter.hasParameterAnnotation(SendSlackSignalRequest::class.java)

	override fun afterBodyRead(
		body: Any,
		inputMessage: HttpInputMessage,
		parameter: MethodParameter,
		targetType: Type,
		converterType: Class<out HttpMessageConverter<*>>
	): Any {
		slackUtils.send(
			listOf(
				SlackMessage.of(
					title = "Subscription request received from method ${parameter.method?.name}.",
					messages = listOf("Request Body: $body")
				)
			)
		)

		return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType)
	}
}
