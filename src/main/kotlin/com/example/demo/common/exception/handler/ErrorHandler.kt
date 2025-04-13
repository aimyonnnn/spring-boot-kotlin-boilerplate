package com.example.demo.common.exception.handler

import com.example.demo.common.exception.AlreadyExistException
import com.example.demo.common.exception.CustomRuntimeException
import com.example.demo.common.exception.NotFoundException
import com.example.demo.common.exception.UnAuthorizedException
import com.example.demo.common.response.ErrorResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class ErrorHandler {
	@ExceptionHandler(CustomRuntimeException::class)
	private fun handleCustomRuntimeException(
		exception: CustomRuntimeException,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<ErrorResponse> {
		val response =
			ErrorResponse.of(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				exception.message ?: HttpStatus.INTERNAL_SERVER_ERROR.name
			)

		logger.error {
			"handleCustomRuntimeException Error - ${httpServletRequest.method} ${httpServletRequest.requestURI} ${exception.message}"
		}

		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(response)
	}

	@ExceptionHandler(ExpiredJwtException::class)
	private fun handleExpiredJwtException(
		exception: ExpiredJwtException,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<ErrorResponse> {
		val response =
			ErrorResponse.of(
				HttpStatus.UNAUTHORIZED.value(),
				exception.message ?: HttpStatus.UNAUTHORIZED.name
			)

		logger.error {
			"handleExpiredJwtException Error - ${httpServletRequest.method} ${httpServletRequest.requestURI} ${exception.message}"
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
	}

	@ExceptionHandler(NotFoundException::class)
	private fun handleNotFoundException(
		exception: NotFoundException,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<ErrorResponse> {
		val response =
			ErrorResponse.of(
				HttpStatus.NOT_FOUND.value(),
				exception.message ?: HttpStatus.NOT_FOUND.name
			)

		logger.error {
			"handleNotFoundException Error - ${httpServletRequest.method} ${httpServletRequest.requestURI} ${exception.message}"
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
	}

	@ExceptionHandler(UnAuthorizedException::class)
	private fun handleUnAuthorizedException(
		exception: UnAuthorizedException,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<ErrorResponse> {
		val response =
			ErrorResponse.of(
				HttpStatus.UNAUTHORIZED.value(),
				exception.message ?: HttpStatus.UNAUTHORIZED.name
			)

		logger.error {
			"handleUnAuthorizedException Error - ${httpServletRequest.method} ${httpServletRequest.requestURI} ${exception.message}"
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
	}

	@ExceptionHandler(AlreadyExistException::class)
	private fun handleAlreadyExistException(
		exception: AlreadyExistException,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<ErrorResponse> {
		val response =
			ErrorResponse.of(
				HttpStatus.CONFLICT.value(),
				exception.message ?: HttpStatus.CONFLICT.name
			)

		logger.error {
			"handleAlreadyExistException Error - ${httpServletRequest.method} ${httpServletRequest.requestURI} ${exception.message}"
		}

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
	}

	@ExceptionHandler(BindException::class)
	private fun handleBindException(
		exception: BindException,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<ErrorResponse> {
		val exceptionMessage =
			exception.bindingResult.fieldErrors.joinToString(", ") {
				"${it.field}: ${it.defaultMessage}"
			}

		val response =
			ErrorResponse.of(
				HttpStatus.BAD_REQUEST.value(),
				exceptionMessage,
				exception.fieldErrors
			)

		logger.error {
			"handleBindException Error - ${httpServletRequest.method} ${httpServletRequest.requestURI} $exceptionMessage"
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
	}

	@ExceptionHandler(MethodArgumentNotValidException::class)
	private fun handleMethodArgumentNotValidException(
		exception: MethodArgumentNotValidException,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<ErrorResponse> {
		val exceptionMessage =
			exception.bindingResult.fieldErrors.joinToString(", ") {
				"${it.field}: ${it.defaultMessage}"
			}

		val response =
			ErrorResponse.of(
				HttpStatus.BAD_REQUEST.value(),
				exceptionMessage,
				exception.fieldErrors
			)

		logger.error {
			"handleMethodArgumentNotValidException Error - ${httpServletRequest.method} ${httpServletRequest.requestURI} $exceptionMessage"
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
	}

	@ExceptionHandler(AuthenticationException::class)
	private fun handleAuthenticationException(
		exception: AuthenticationException,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<ErrorResponse> {
		val response =
			ErrorResponse.of(
				HttpStatus.UNAUTHORIZED.value(),
				exception.message ?: HttpStatus.UNAUTHORIZED.name
			)

		logger.error {
			"handleAuthenticationException Error - ${httpServletRequest.method} ${httpServletRequest.requestURI} ${exception.message}"
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
	}

	@ExceptionHandler(AccessDeniedException::class)
	private fun handleAccessDeniedExceptionException(
		exception: AccessDeniedException,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<ErrorResponse> {
		val response =
			ErrorResponse.of(
				HttpStatus.FORBIDDEN.value(),
				exception.message ?: HttpStatus.FORBIDDEN.name
			)

		logger.error {
			"handleAccessDeniedExceptionException Error - ${httpServletRequest.method} ${httpServletRequest.requestURI} ${exception.message}"
		}

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response)
	}

	@ExceptionHandler(NoHandlerFoundException::class)
	private fun handleNoHandlerFoundException(
		exception: NoHandlerFoundException,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<ErrorResponse> {
		val response =
			ErrorResponse.of(
				HttpStatus.NOT_FOUND.value(),
				exception.message ?: HttpStatus.NOT_FOUND.name,
				exception.requestHeaders
			)

		logger.error {
			"handleNoHandlerFoundException Error - ${httpServletRequest.method} ${httpServletRequest.requestURI} ${exception.message}"
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
	}

	@ExceptionHandler(Exception::class)
	private fun handleException(
		exception: Exception,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<ErrorResponse> {
		val response =
			ErrorResponse.of(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				exception.message ?: HttpStatus.INTERNAL_SERVER_ERROR.name
			)

		logger.error {
			"handleException Error - ${httpServletRequest.method} ${httpServletRequest.requestURI} ${exception.message}"
		}

		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(response)
	}
}
