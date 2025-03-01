package com.example.demo.security

import com.example.demo.user.constant.UserRole
import com.example.demo.user.entity.User

data class SecurityUserItem(
  val userId: Long,
  val role: UserRole,
  val name: String,
  val email: String
) {
  companion object {
    fun from(user: User): SecurityUserItem =
      SecurityUserItem(
        userId = user.id,
        role = user.role,
        name = user.name,
        email = user.email
      )
  }
}
