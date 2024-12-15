package com.example.demo.kotest.auth.api

import com.example.demo.auth.api.AuthController
import com.example.demo.auth.application.AuthService
import com.example.demo.auth.dto.serve.request.SignInRequest
import com.example.demo.auth.dto.serve.response.RefreshAccessTokenResponse
import com.example.demo.auth.dto.serve.response.SignInResponse
import com.example.demo.kotest.common.BaseIntegrationController
import com.example.demo.kotest.common.security.SecurityListerFactory
import com.example.demo.security.SecurityUserItem
import com.example.demo.security.exception.RefreshTokenNotFoundException
import com.example.demo.user.entity.User
import com.example.demo.user.exception.UserNotFoundException
import com.example.demo.user.exception.UserUnAuthorizedException
import com.ninjasquad.springmockk.MockkBean
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.kotest.core.annotation.Tags
import io.mockk.every
import io.mockk.justRun
import org.instancio.Instancio
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ActiveProfiles("test")
@Tags("kotest-integration-test")
@WebMvcTest(
  AuthController::class
)
class AuthIntegrationControllerTests : BaseIntegrationController() {
  @MockkBean
  private lateinit var authService: AuthService

  private val user: User = Instancio.create(User::class.java)
  private val defaultUserEmail = "awakelife93@gmail.com"
  private val defaultUserPassword = "test_password_123!@"
  private val defaultAccessToken =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\n" +  //
      ""

  init {
    initialize()

    Given("POST /api/v1/auth/signIn") {
      val mockSignInRequest = Instancio.create(SignInRequest::class.java)
      val signInRequest: SignInRequest = mockSignInRequest.copy(
        email = defaultUserEmail,
        password = defaultUserPassword
      )

      When("Success POST /api/v1/auth/signIn") {

        every { authService.signIn(any<SignInRequest>()) } returns SignInResponse.of(user, defaultAccessToken)

        Then("Call POST /api/v1/auth/signIn") {
          mockMvc
            .perform(
              MockMvcRequestBuilders
                .post("/api/v1/auth/signIn")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .content(objectMapper.writeValueAsString(signInRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(commonStatus))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").value(user.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(user.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(user.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value(user.role.name))
        }
      }

      When("Field Valid Exception POST /api/v1/auth/signIn") {
        val wrongSignInRequest: SignInRequest = signInRequest.copy(
          email = "wrong_email_format",
          password = "1234"
        )

        Then("POST /api/v1/auth/signIn") {
          mockMvc
            .perform(
              MockMvcRequestBuilders
                .post("/api/v1/auth/signIn")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .content(objectMapper.writeValueAsString(wrongSignInRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
              MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value())
            )
            // email:field email is not email format, password:field password is min size 8 and max size 20,
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty)
        }
      }

      When("UnAuthorized Exception POST /api/v1/auth/signIn") {
        val userUnAuthorizedException = UserUnAuthorizedException(
          user.id
        )

        every { authService.signIn(any<SignInRequest>()) } throws userUnAuthorizedException

        Then("Call POST /api/v1/auth/signIn") {
          mockMvc
            .perform(
              MockMvcRequestBuilders
                .post("/api/v1/auth/signIn")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .content(objectMapper.writeValueAsString(signInRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.UNAUTHORIZED.value()))
            .andExpect(
              MockMvcResultMatchers.jsonPath("$.message").value(userUnAuthorizedException.message)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty)
        }
      }

      When("Not Found Exception POST /api/v1/auth/signIn") {
        val userNotFoundException = UserNotFoundException(user.id)

        every { authService.signIn(any<SignInRequest>()) } throws userNotFoundException

        Then("Call POST /api/v1/auth/signIn") {
          mockMvc
            .perform(
              MockMvcRequestBuilders
                .post("/api/v1/auth/signIn")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .content(objectMapper.writeValueAsString(signInRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
            .andExpect(
              MockMvcResultMatchers.jsonPath("$.message").value(userNotFoundException.message)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty)
        }
      }
    }

    Given("POST /api/v1/auth/signOut") {

      When("Success POST /api/v1/auth/signOut") {

        justRun { authService.signOut(any<Long>()) }

        Then("Call POST /api/v1/auth/signOut") {
          mockMvc
            .perform(
              MockMvcRequestBuilders
                .post("/api/v1/auth/signOut")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(commonStatus))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
        }
      }
    }

    Given("POST /api/v1/auth/refresh") {

      When("Success POST /api/v1/auth/refresh") {

        every { authService.refreshAccessToken(any<SecurityUserItem>()) } returns RefreshAccessTokenResponse.of(
          defaultAccessToken
        )

        Then("Call POST /api/v1/auth/refresh") {
          mockMvc
            .perform(
              MockMvcRequestBuilders
                .post("/api/v1/auth/refresh")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(commonStatus))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(commonMessage))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value(defaultAccessToken))
        }
      }

      When("Not Found Exception POST /api/v1/auth/refresh") {
        val refreshTokenNotFoundException = RefreshTokenNotFoundException(user.id)

        every { authService.refreshAccessToken(any<SecurityUserItem>()) } throws refreshTokenNotFoundException

        Then("Call POST /api/v1/auth/refresh") {
          mockMvc
            .perform(
              MockMvcRequestBuilders
                .post("/api/v1/auth/refresh")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(
              MockMvcResultMatchers.jsonPath("$.code")
                .value(HttpStatus.UNAUTHORIZED.value())
            )
            .andExpect(
              MockMvcResultMatchers.jsonPath("$.message")
                .value(refreshTokenNotFoundException.message)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty)
        }
      }

      When("Expired Exception POST /api/v1/auth/refresh") {
        val claims = Instancio.create(Claims::class.java)
        val expiredJwtException = ExpiredJwtException(
          null,
          claims,
          "JWT expired at ?. Current time: ?, a difference of ? milliseconds.  Allowed clock skew: ? millis"
        )

        every { authService.refreshAccessToken(any<SecurityUserItem>()) } throws expiredJwtException

        Then("Call POST /api/v1/auth/refresh") {
          mockMvc
            .perform(
              MockMvcRequestBuilders
                .post("/api/v1/auth/refresh")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(
              MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.UNAUTHORIZED.value())
            )
            .andExpect(
              MockMvcResultMatchers.jsonPath("$.message").value(expiredJwtException.message)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isEmpty)
        }
      }
    }

    Given("Spring Security Context is not set.") {

      When("Unauthorized Exception POST /api/v1/auth/signOut") {

        Then("Call POST /api/v1/auth/signOut").config(tags = setOf(SecurityListerFactory.NonSecurityOption)) {
          mockMvc
            .perform(
              MockMvcRequestBuilders
                .post("/api/v1/auth/signOut")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
        }
      }

      When("Unauthorized Exception POST /api/v1/auth/refresh") {

        Then("Call POST /api/v1/auth/refresh").config(tags = setOf(SecurityListerFactory.NonSecurityOption)) {
          mockMvc
            .perform(
              MockMvcRequestBuilders
                .post("/api/v1/auth/refresh")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
        }
      }
    }
  }
}
