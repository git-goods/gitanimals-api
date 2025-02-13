package org.gitanimals.identity.controller

import org.gitanimals.identity.app.Token
import org.gitanimals.identity.app.UserFacade
import org.gitanimals.identity.controller.response.UserResponse
import org.gitanimals.identity.domain.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userFacade: UserFacade,
    private val userService: UserService,
) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users")
    fun getUserByToken(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
    ): UserResponse {
        val user = userFacade.getUserByToken(Token.from(token))
        return UserResponse.from(user)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/internals/users/{user-id}")
    fun getUserById(
        @PathVariable("user-id") userId: Long,
    ): UserResponse {
        val user = userService.getUserById(userId)
        return UserResponse.from(user)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/internals/users/by-name/{name}")
    fun getUserByName(
        @PathVariable("name") username: String,
    ): UserResponse {
        val user = userService.getUserByName(username)
        return UserResponse.from(user)
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/internals/users/points/decreases")
    fun decreaseUserPoints(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("point") point: Long,
        @RequestParam("idempotency-key") idempotencyKey: String,
    ) {
        userFacade.decreasePoint(Token.from(token), idempotencyKey, point)
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/internals/users/points/increases")
    fun increaseUserPoints(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("point") point: Long,
        @RequestParam("idempotency-key") idempotencyKey: String,
    ) {
        userFacade.increasePoint(Token.from(token), idempotencyKey, point)
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/internals/users/points/decreases/{user-id}")
    fun decreaseUserPointsById(
        @PathVariable("user-id") userId: Long,
        @RequestParam("point") point: Long,
        @RequestParam("idempotency-key") idempotencyKey: String,
    ) {
        userService.decreasePoint(userId, idempotencyKey, point)
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/internals/users/points/increases/{user-id}")
    fun increaseUserPointsById(
        @PathVariable("user-id") userId: Long,
        @RequestParam("point") point: Long,
        @RequestParam("idempotency-key") idempotencyKey: String,
    ) {
        userService.increasePoint(userId, idempotencyKey, point)
    }
}
