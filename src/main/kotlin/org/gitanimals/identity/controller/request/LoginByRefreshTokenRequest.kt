package org.gitanimals.identity.controller.request

data class LoginByRefreshTokenRequest(
    val refreshToken: String
)
