package org.gitanimals.coupon.infra

import io.jsonwebtoken.JwtException
import org.gitanimals.core.filter.MDCFilter
import org.gitanimals.coupon.app.IdentityApi
import org.gitanimals.coupon.app.response.UserResponse
import org.slf4j.MDC
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
            .header(MDCFilter.TRACE_ID, MDC.get(MDCFilter.TRACE_ID))
            .exchange { _, response ->
                runCatching {
                    response.bodyTo(UserResponse::class.java)
                }.getOrElse {
                    throw JwtException("Authorization failed", it)
                }
            }
    }
}
