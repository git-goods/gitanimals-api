package org.gitanimals.gotcha.app

import org.gitanimals.gotcha.app.response.UserResponse

interface UserApi {

    fun getUserByToken(token: String): UserResponse

    fun decreasePoint(token: String, idempotencyKey: String, point: Long)

    fun increasePoint(token: String, idempotencyKey: String, point: Long)
}
