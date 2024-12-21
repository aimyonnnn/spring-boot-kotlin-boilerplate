package com.example.demo.common.interceptor

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class LoggingForInterceptor : HandlerInterceptor {

  @Throws(Exception::class)
  override fun postHandle(
    request: HttpServletRequest,
    response: HttpServletResponse,
    handler: Any,
    modelAndView: ModelAndView?
  ) {
    logger.info {
      "${request.requestURI} - ${response.status}"
    }

    super.postHandle(request, response, handler, null)
  }
}
