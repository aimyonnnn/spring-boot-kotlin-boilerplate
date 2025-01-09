package com.example.demo.utils

import com.example.demo.common.response.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

private val logger = KotlinLogging.logger {}

object SecurityUtils {
  private val objectMapper = ObjectMapper()

  fun sendErrorResponse(
    httpServletRequest: HttpServletRequest,
    httpServletResponse: HttpServletResponse,
    exception: Throwable,
    message: String = ""
  ) {
    val errorResponse =
      ErrorResponse.of(
        HttpStatus.UNAUTHORIZED.value(),
        message
      )

    logger.error {
      "Security Filter sendErrorResponse - ${httpServletRequest.method} ${httpServletRequest.requestURI} " +
        "$message ${exception.message ?: "Security Filter Error"}"
    }

    with(httpServletResponse) {
      status = HttpStatus.UNAUTHORIZED.value()
      contentType = MediaType.APPLICATION_JSON_VALUE

      runCatching { writer.write(objectMapper.writeValueAsString(errorResponse)) }
        .onFailure { logger.error { it.message } }
    }
  }
}
