package com.example.demo.mockito.common.security

import com.example.demo.mockito.common.BaseIntegrationController
import com.example.demo.security.component.provider.JWTProvider
import org.springframework.test.context.bean.override.mockito.MockitoBean

open class SecurityItem : BaseIntegrationController() {
  @MockitoBean
  protected lateinit var jwtProvider: JWTProvider
}
