package org.gitanimals.inbox.infra

import io.jsonwebtoken.JwtException
import org.gitanimals.inbox.app.IdentityApi
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component("shop.RestIdentityApi")
class RestIdentityApi(
    @Qualifier("shop.identityRestClient") private val restClient: RestClient,
    @Value("\${internal.secret}") private val internalSecret: String,
) : IdentityApi {

    override fun getUserByToken(token: String): IdentityApi.UserResponse {
        return restClient.get()
            .uri("/users")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange { _, response ->
                runCatching {
                    response.bodyTo(IdentityApi.UserResponse::class.java)
                }.getOrElse {
                    if (response.statusCode.is4xxClientError) {
                        throw JwtException("Authorization failed")
                    }

                    throw IllegalStateException(it)
                }
            }
    }
}
