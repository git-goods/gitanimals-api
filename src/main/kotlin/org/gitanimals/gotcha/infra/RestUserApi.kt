package org.gitanimals.gotcha.infra

import org.gitanimals.gotcha.app.UserApi
import org.gitanimals.gotcha.app.response.UserResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class RestUserApi(
    @Qualifier("userRestClient") private val restClient: RestClient,
) : UserApi {

    override fun getUserByToken(token: String): UserResponse {
        return restClient.get()
            .uri("/users")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange { _, response ->
                response.bodyTo(UserResponse::class.java)
                    ?: throw IllegalArgumentException("Authorization failed")
            }
    }

    override fun decreasePoint(token: String, idempotencyKey: String, point: String) {
        return restClient.post()
            .uri("/internals/users/points/decreases?point=$point&idempotency-key=$idempotencyKey")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange { _, response ->
                if (response.statusCode.is2xxSuccessful) {
                    return@exchange
                }
                throw IllegalArgumentException(
                    "Cannot decrease points cause \"${response.bodyTo(String::class.java)}\""
                )
            }
    }

    override fun increasePoint(token: String, idempotencyKey: String, point: String) {
        return restClient.post()
            .uri("/internals/users/points/increases?point=$point&idempotency-key=$idempotencyKey")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange { _, response ->
                if (response.statusCode.is2xxSuccessful) {
                    return@exchange
                }
                throw IllegalArgumentException(
                    "Cannot decrease points cause \"${response.bodyTo(String::class.java)}\""
                )
            }
    }
}
