package org.gitanimals.inbox.app

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.GetExchange

fun interface IdentityApi {

    @GetExchange("/users")
    fun getUserByToken(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String): UserResponse

    data class UserResponse(
        val id: String,
        val username: String,
        val points: String,
    )
}
