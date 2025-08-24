package org.gitanimals.identity.controller

import org.gitanimals.identity.app.RefreshTokenFacade
import org.gitanimals.identity.controller.request.LoginByRefreshTokenRequest
import org.gitanimals.identity.controller.response.RefreshTokenResponse
import org.gitanimals.identity.controller.response.TokenResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*

@RestController
class RefreshTokenController(
    private val refreshTokenFacade: RefreshTokenFacade,
) {

    @PostMapping("/users/refresh-tokens")
    fun generateRefreshToken(
        @RequestHeader("Login-Secret") loginSecret: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String,
    ): RefreshTokenResponse {
        val refreshToken = refreshTokenFacade.generateRefreshToken(
            loginSecret = loginSecret,
            token = authorization,
        )

        return RefreshTokenResponse(refreshToken)
    }

    @PostMapping("/logins/refresh-tokens")
    fun loginByRefreshToken(
        @RequestHeader("Login-Secret") loginSecret: String,
        @RequestBody loginByRefreshTokenRequest: LoginByRefreshTokenRequest,
    ): TokenResponse {
        val token = refreshTokenFacade.getTokenByRefreshToken(
            loginSecret = loginSecret,
            refreshToken = loginByRefreshTokenRequest.refreshToken,
        )

        return TokenResponse(token.withType())
    }
}
