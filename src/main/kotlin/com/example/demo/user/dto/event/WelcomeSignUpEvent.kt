package com.example.demo.user.dto.event

import com.example.demo.user.entity.User

data class WelcomeSignUpEvent(
	val email: String,
	val name: String
) {
	companion object {
		fun from(user: User): WelcomeSignUpEvent =
			with(user) {
				WelcomeSignUpEvent(
					email = email,
					name = name
				)
			}
	}
}
