package com.example.demo.user.exception

import com.example.demo.common.exception.AlreadyExistException

class AlreadyUserExistException : AlreadyExistException {
  constructor(userId: Long) : super(
    "Already User Exist userId = $userId"
  )

  constructor(email: String) : super(
    "Already User Exist email = $email"
  )
}
