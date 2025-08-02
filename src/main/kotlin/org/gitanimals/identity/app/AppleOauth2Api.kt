package org.gitanimals.identity.app

import org.springframework.web.service.annotation.GetExchange

fun interface AppleOauth2Api {

    @GetExchange("/auth/keys")
    fun getAuthKeys(): AppleAuthKeyResponse

    data class AppleAuthKeyResponse(
        val keys: List<AuthKey>
    ) {
        data class AuthKey(
            val kty: String,
            val kid: String,
            val use: String,
            val alg: String,
            val n: String,
            val e: String,
        )
    }
}
