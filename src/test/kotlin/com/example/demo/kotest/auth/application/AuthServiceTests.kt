package com.example.demo.kotest.auth.application

import com.example.demo.auth.application.AuthService
import com.example.demo.auth.dto.serve.request.RefreshAccessTokenRequest
import com.example.demo.auth.dto.serve.request.SignInRequest
import com.example.demo.auth.dto.serve.response.RefreshAccessTokenResponse
import com.example.demo.auth.dto.serve.response.SignInResponse
import com.example.demo.security.SecurityUserItem
import com.example.demo.security.component.provider.TokenProvider
import com.example.demo.security.exception.RefreshTokenNotFoundException
import com.example.demo.user.application.UserService
import com.example.demo.user.entity.User
import com.example.demo.user.exception.UserNotFoundException
import com.example.demo.user.exception.UserUnAuthorizedException
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.instancio.Instancio
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class AuthServiceTests :
	BehaviorSpec({
		val tokenProvider = mockk<TokenProvider>()
		val userService = mockk<UserService>()
		val authService = mockk<AuthService>()

		val user: User = Instancio.create(User::class.java)
		val defaultAccessToken =
			"""
      eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
      eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
      """

		Given("Sign In") {
			val signInRequest: SignInRequest = Instancio.create(SignInRequest::class.java)

			When("Success Sign In") {

				every { tokenProvider.createFullTokens(any<User>()) } returns defaultAccessToken

				every { userService.validateAuthReturnUser(any<SignInRequest>()) } returns user

				every { authService.signIn(any<SignInRequest>()) } returns SignInResponse.from(user, defaultAccessToken)

				val signInResponse = authService.signIn(signInRequest)

				Then("Assert Sign In Response") {
					signInResponse shouldNotBeNull {
						email shouldBe user.email
						name shouldBe user.name
						accessToken shouldBe defaultAccessToken
					}
				}
			}

			When("User Not Found Exception") {

				every { userService.validateAuthReturnUser(any<SignInRequest>()) } throws UserNotFoundException(user.id)

				every { authService.signIn(any<SignInRequest>()) } throws UserNotFoundException(user.id)

				shouldThrowExactly<UserNotFoundException> { authService.signIn(signInRequest) }
			}

			When("User Unauthorized Exception") {

				every { userService.validateAuthReturnUser(any<SignInRequest>()) } throws UserUnAuthorizedException(user.email)

				every { authService.signIn(any<SignInRequest>()) } throws UserUnAuthorizedException(user.email)

				shouldThrowExactly<UserUnAuthorizedException> { authService.signIn(signInRequest) }
			}
		}

		Given("Sign Out") {

			When("Success Sign Out") {

				justRun {
					tokenProvider.deleteRefreshToken(any<Long>())
					authService.signOut(any<Long>())
				}

				tokenProvider.deleteRefreshToken(user.id)

				authService.signOut(user.id)

				verify(exactly = 1) {
					tokenProvider.deleteRefreshToken(user.id)
					authService.signOut(user.id)
				}
			}
		}

		Given("Refresh Access Token") {
			val securityUserItem: SecurityUserItem =
				Instancio.create(
					SecurityUserItem::class.java
				)
			val refreshAccessTokenRequest: RefreshAccessTokenRequest =
				Instancio.create(
					RefreshAccessTokenRequest::class.java
				)

			When("Success Refresh Access Token") {

				every { tokenProvider.refreshAccessToken(any<SecurityUserItem>()) } returns defaultAccessToken

				every { authService.refreshAccessToken(any<RefreshAccessTokenRequest>()) } returns
					RefreshAccessTokenResponse.of(
						defaultAccessToken
					)

				val refreshAccessTokenResponse = authService.refreshAccessToken(refreshAccessTokenRequest)

				Then("Assert Refresh Access Token Response") {
					refreshAccessTokenResponse shouldNotBeNull {
						accessToken shouldBe defaultAccessToken
					}
				}
			}

			When("Refresh Access Token Not Found Exception") {

				every { tokenProvider.refreshAccessToken(any<SecurityUserItem>()) } throws RefreshTokenNotFoundException(user.id)

				shouldThrowExactly<RefreshTokenNotFoundException> { tokenProvider.refreshAccessToken(securityUserItem) }
			}
		}
	})
