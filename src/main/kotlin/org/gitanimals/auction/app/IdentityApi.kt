package org.gitanimals.auction.app

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

interface IdentityApi {

    @GetExchange("/users")
    fun getUserByToken(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String): UserResponse

    @GetExchange("/internals/users/{userId}")
    fun getUserById(@PathVariable("userId") userId: Long): UserResponse

    @PostExchange("/internals/users/points/decreases")
    fun decreasePoint(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("idempotency-key") idempotencyKey: String,
        @RequestParam("point") point: String,
    )

    @PostExchange("/internals/users/points/increases")
    fun increasePoint(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("idempotency-key") idempotencyKey: String,
        @RequestParam("point") point: String,
    )

    @PostExchange("/internals/users/points/increases/{userId}")
    fun increasePointById(
        @PathVariable("userId") userId: Long,
        @RequestParam("idempotency-key") idempotencyKey: String,
        @RequestParam("point") point: String,
    )

    @PostExchange("/internals/users/points/decreases/{userId}")
    fun decreasePointById(
        @PathVariable("userId") userId: Long,
        @RequestParam("idempotency-key") idempotencyKey: String,
        @RequestParam("point") point: String,
    )


    data class UserResponse(
        val id: String,
        val username: String,
        val points: String,
    )
}
