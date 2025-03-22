package com.example.demo.security.config

import com.example.demo.security.component.CustomAccessDeniedHandler
import com.example.demo.security.component.CustomAuthenticationEntryPoint
import com.example.demo.security.component.provider.AuthProvider
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.web.SecurityFilterChain

@Profile("local")
@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(securedEnabled = true)
class LocalSecurityConfig(
  private val authProvider: AuthProvider,
  private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
  private val customAccessDeniedHandler: CustomAccessDeniedHandler
) {
  @Bean
  @Throws(Exception::class)
  fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager = authenticationConfiguration.authenticationManager

  @Bean
  @Throws(Exception::class)
  fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain =
    authProvider
      .defaultSecurityFilterChain(httpSecurity)
      .headers {
        it.frameOptions { options ->
          options.sameOrigin()
        }
      }.authorizeHttpRequests { request ->
        request
          .requestMatchers(*authProvider.whiteListDefaultEndpoints(), *authProvider.ignoreListDefaultEndpoints())
          .permitAll()
          .requestMatchers(PathRequest.toH2Console())
          .permitAll()
          .anyRequest()
          .authenticated()
      }.exceptionHandling { exceptionHandling: ExceptionHandlingConfigurer<HttpSecurity?> ->
        exceptionHandling
          .authenticationEntryPoint(customAuthenticationEntryPoint)
          .accessDeniedHandler(customAccessDeniedHandler)
      }.build()
}
