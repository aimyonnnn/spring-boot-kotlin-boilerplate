package com.example.demo.mockito.common

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext

open class BaseIntegrationController {
  @Autowired
  protected lateinit var webApplicationContext: WebApplicationContext

  @Autowired
  protected lateinit var objectMapper: ObjectMapper

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
