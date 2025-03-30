package org.gitanimals.inbox.app

import org.springframework.web.service.annotation.GetExchange

fun interface IdentityApi {

    @GetExchange("/users")
    fun getUserByToken(token: String): UserResponse

    data class UserResponse(
        val id: String,
        val username: String,
        val points: String,
    )
}
