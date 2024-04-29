package org.gitanimals.auction.app

interface IdentityApi {

    fun getUserByToken(token: String): UserResponse

    fun decreasePoint(token: String, idempotencyKey: String, point: String)

    fun increasePoint(token: String, idempotencyKey: String, point: String)

    data class UserResponse(
        val id: String,
        val username: String,
        val points: String,
    )
}
