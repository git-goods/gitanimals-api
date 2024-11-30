package org.gitanimals.auction.infra

import io.jsonwebtoken.JwtException
import org.gitanimals.auction.app.IdentityApi
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component("auction.RestIdentityApi")
class RestIdentityApi(
    @Qualifier("auction.identityRestClient") private val restClient: RestClient,
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

    override fun getUserById(userId: Long): IdentityApi.UserResponse {
        return restClient.get()
            .uri("/users/$userId")
            .header(INTERNAL_SECRET_KEY, internalSecret)
            .exchange { _, response ->
                runCatching {
                    response.bodyTo(IdentityApi.UserResponse::class.java)
                }.getOrElse {
                    if (response.statusCode.is4xxClientError) {
                        throw IllegalArgumentException("Cannot find user by id \"$userId\"")
                    }

                    throw IllegalStateException(it)
                }
            }
    }

    override fun decreasePoint(token: String, idempotencyKey: String, point: String) {
        return restClient.post()
            .uri("/internals/users/points/decreases?point=$point&idempotency-key=$idempotencyKey")
            .header(HttpHeaders.AUTHORIZATION, token)
            .header(INTERNAL_SECRET_KEY, internalSecret)
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
            .header(INTERNAL_SECRET_KEY, internalSecret)
            .exchange { _, response ->
                if (response.statusCode.is2xxSuccessful) {
                    return@exchange
                }
                throw IllegalArgumentException(
                    "Cannot decrease points cause \"${response.bodyTo(String::class.java)}\""
                )
            }
    }

    override fun decreasePointById(userId: Long, idempotencyKey: String, point: String) {
        return restClient.post()
            .uri("/internals/users/points/decreases/$userId?point=$point&idempotency-key=$idempotencyKey")
            .header(INTERNAL_SECRET_KEY, internalSecret)
            .exchange { _, response ->
                if (response.statusCode.is2xxSuccessful) {
                    return@exchange
                }
                throw IllegalArgumentException(
                    "Cannot decrease points cause \"${response.bodyTo(String::class.java)}\""
                )
            }
    }

    override fun increasePointById(userId: Long, idempotencyKey: String, point: String) {
        return restClient.post()
            .uri("/internals/users/points/increases/$userId?point=$point&idempotency-key=$idempotencyKey")
            .header(INTERNAL_SECRET_KEY, internalSecret)
            .exchange { _, response ->
                if (response.statusCode.is2xxSuccessful) {
                    return@exchange
                }
                throw IllegalArgumentException(
                    "Cannot decrease points cause \"${response.bodyTo(String::class.java)}\""
                )
            }
    }

    private companion object {
        private const val INTERNAL_SECRET_KEY = "Internal-Secret"
    }
}
