package com.example.demo.security.component.provider

import com.example.demo.security.SecurityUserItem
import com.example.demo.security.UserAdapter
import com.example.demo.security.service.impl.UserDetailsServiceImpl
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import java.util.Date
import java.util.concurrent.TimeUnit

@Component
class JWTProvider(
	private val userDetailsServiceImpl: UserDetailsServiceImpl,
	@Value("\${auth.jwt.secret}") private val secretKey: String,
	@Value("\${auth.jwt.access-expire}") private val accessExpireTime: Long,
	@Value("\${auth.jwt.refresh-expire}") val refreshExpireTime: Long = 0L
) {
	fun createAccessToken(securityUserItem: SecurityUserItem): String = createToken(securityUserItem, true)

	fun createRefreshToken(securityUserItem: SecurityUserItem): String = createToken(securityUserItem, false)

	fun createToken(
		securityUserItem: SecurityUserItem,
		isAccessToken: Boolean
	): String {
		val claims: Claims =
			Jwts
				.claims()
				.setSubject(securityUserItem.userId.toString())
		claims["email"] = securityUserItem.email
		claims["role"] = securityUserItem.role

		val expireTime = if (isAccessToken) accessExpireTime else refreshExpireTime
		val now = Date()
		val expiration = Date(now.time + TimeUnit.SECONDS.toMillis(expireTime))

		return Jwts
			.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(expiration)
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact()
	}

	fun refreshAccessToken(
		securityUserItem: SecurityUserItem,
		refreshToken: String
	): String {
		getAuthentication(refreshToken)
		return createAccessToken(securityUserItem)
	}

	fun validateToken(token: String): Boolean {
		val usernamePasswordAuthenticationToken = getAuthentication(token)
		return usernamePasswordAuthenticationToken.isAuthenticated
	}

	fun generateRequestToken(request: HttpServletRequest): String? {
		val token =
			request
				.getHeader(HttpHeaders.AUTHORIZATION)
				?.takeIf { it.startsWith("Bearer") }
				?.substring(7)

		return token
	}

	fun getAuthentication(token: String): UsernamePasswordAuthenticationToken = getAuthentication(token, false)

	fun getAuthentication(
		token: String,
		isRefresh: Boolean
	): UsernamePasswordAuthenticationToken {
		val claims: Claims = generateClaims(token, isRefresh)
		val userAdapter: UserAdapter =
			userDetailsServiceImpl.loadUserByUsername(
				claims.subject
			) as UserAdapter

		return UsernamePasswordAuthenticationToken(
			userAdapter,
			null,
			userAdapter.authorities
		)
	}

	private fun generateClaims(
		token: String,
		isRefresh: Boolean
	): Claims =
		runCatching {
			Jwts
				.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.body
		}.getOrElse {
			if (it is ExpiredJwtException && isRefresh) it.claims else throw it
		}
}
