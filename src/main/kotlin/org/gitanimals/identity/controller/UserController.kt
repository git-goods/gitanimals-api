package org.gitanimals.identity.controller

import org.gitanimals.identity.app.Token
import org.gitanimals.identity.app.UserFacade
import org.gitanimals.identity.controller.response.UserResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userFacade: UserFacade,
) {

    @GetMapping("/users")
    fun getUserByToken(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
    ): UserResponse {
        val user = userFacade.getUserByToken(Token.from(token))
        return UserResponse.from(user)
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/users/points/decreases")
    fun decreaseUserPoints(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("point") point: Long,
        @RequestParam("idempotency-key") idempotencyKey: String,
    ) {
        userFacade.decreasePoint(Token.from(token), idempotencyKey, point)
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/users/points/increases")
    fun increaseUserPoints(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("point") point: Long,
        @RequestParam("idempotency-key") idempotencyKey: String,
    ) {
        userFacade.increasePoint(Token.from(token), idempotencyKey, point)
    }
}
