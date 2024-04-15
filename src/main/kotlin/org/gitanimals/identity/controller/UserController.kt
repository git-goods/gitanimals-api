package org.gitanimals.identity.controller

import org.gitanimals.identity.app.Token
import org.gitanimals.identity.app.UserFacade
import org.gitanimals.identity.controller.response.UserResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

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
}
