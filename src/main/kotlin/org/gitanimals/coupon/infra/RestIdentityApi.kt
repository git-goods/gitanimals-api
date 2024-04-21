package org.gitanimals.coupon.infra

import org.gitanimals.coupon.app.IdentityApi
import org.gitanimals.coupon.app.response.UserResponse
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class RestIdentityApi : IdentityApi {

    private val restClient = RestClient.create("https://api.gitanimals.org")

    override fun getUserByToken(token: String): UserResponse {
        return restClient.get()
            .uri("/users")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange { _, response ->
                response.bodyTo(UserResponse::class.java)
                    ?: throw IllegalArgumentException("Authorization failed")
            }
    }
}
