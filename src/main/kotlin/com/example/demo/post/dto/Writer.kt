package com.example.demo.post.dto

import com.example.demo.user.entity.User

data class Writer(
	val userId: Long,
	val email: String,
	val name: String
) {
	companion object {
		fun from(user: User): Writer =
			with(user) {
				Writer(
					userId = id,
					email = email,
					name = name
				)
			}
	}
}
