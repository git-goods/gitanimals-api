package org.gitanimals.auction.infra

import org.gitanimals.auction.app.IdentityApi
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component("auction.RestIdentityApi")
class RestIdentityApi(
    @Qualifier("auction.identityRestClient") private val restClient: RestClient,
) : IdentityApi {

    override fun getUserByToken(token: String): IdentityApi.UserResponse {
        return restClient.get()
            .uri("/users")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange { _, response ->
                runCatching {
                    response.bodyTo(IdentityApi.UserResponse::class.java)
                }.getOrElse {
                    require(!response.statusCode.is4xxClientError) { "Authorization failed" }

                    throw IllegalStateException(it)
                }
            }
    }
}
