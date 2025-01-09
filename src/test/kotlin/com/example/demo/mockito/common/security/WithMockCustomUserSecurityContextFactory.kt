package com.example.demo.mockito.common.security

import com.example.demo.security.SecurityUserItem
import com.example.demo.security.UserAdapter
import com.example.demo.user.constant.UserRole
import com.example.demo.user.entity.User
import org.instancio.Instancio
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithMockCustomUserSecurityContextFactory : WithSecurityContextFactory<WithMockCustomUser> {
  override fun createSecurityContext(annotation: WithMockCustomUser): SecurityContext {
    val securityContext = SecurityContextHolder.createEmptyContext()
    val user = Instancio.create(User::class.java)
    val securityUserItem =
      SecurityUserItem.of(
        user.also {
          with(annotation) {
            it.id = id.toLong()
            it.email = email
            it.name = name
            it.role = UserRole.valueOf(role)
          }
        }
      )

    val userAdapter = UserAdapter(securityUserItem)

    val usernamePasswordAuthenticationToken =
      UsernamePasswordAuthenticationToken(
        userAdapter,
        null,
        userAdapter.authorities
      )

    securityContext.authentication = usernamePasswordAuthenticationToken
    return securityContext
  }
}
