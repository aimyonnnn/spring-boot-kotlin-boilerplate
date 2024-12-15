package com.example.demo.mockito.common.security

import com.example.demo.mockito.common.BaseIntegrationController
import com.example.demo.security.component.provider.JWTProvider
import org.springframework.boot.test.mock.mockito.MockBean

open class SecurityItem : BaseIntegrationController() {
  @MockBean
  protected lateinit var jwtProvider: JWTProvider
}
