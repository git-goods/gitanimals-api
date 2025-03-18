package org.gitanimals.quiz.app

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

interface IdentityApi {

    @GetExchange("/users")
    fun getUserByToken(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String): UserResponse

    @PostExchange("/internals/users/points/increases/{user-id}")
    fun increaseUserPointsById(
        @PathVariable("user-id") userId: Long,
        @RequestParam("point") point: Long,
        @RequestParam("idempotency-key") idempotencyKey: String,
    )

    @PostExchange("/internals/users/points/decreases/{user-id}")
    fun decreaseUserPointsById(
        @PathVariable("user-id") userId: Long,
        @RequestParam("point") point: Long,
        @RequestParam("idempotency-key") idempotencyKey: String,
    )

    data class UserResponse(
        val id: String,
        val username: String,
        val points: String,
    )
}
