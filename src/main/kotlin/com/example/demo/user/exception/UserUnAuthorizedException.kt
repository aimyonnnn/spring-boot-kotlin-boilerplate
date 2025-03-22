package com.example.demo.user.exception

import com.example.demo.common.exception.UnAuthorizedException

class UserUnAuthorizedException : UnAuthorizedException {
	constructor(userId: Long) : super(
		"User Un Authorized userId = $userId"
	)

	constructor(email: String) : super(
		"User Un Authorized email = $email"
	)
}
