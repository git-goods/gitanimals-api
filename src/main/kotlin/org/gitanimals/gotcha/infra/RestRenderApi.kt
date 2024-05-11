package org.gitanimals.gotcha.infra

import org.gitanimals.gotcha.app.RenderApi
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class RestRenderApi(
    @Qualifier("renderRestClient") private val restClient: RestClient,
) : RenderApi {

    override fun addPersona(token: String, idempotencyKey: String, personaName: String): String {
        return restClient.post()
            .uri("/internals/personas?idempotency-key=$idempotencyKey")
            .header(HttpHeaders.AUTHORIZATION, token)
            .body(
                mapOf("name" to personaName)
            )
            .exchange { _, response ->
                if (response.statusCode.is2xxSuccessful) {
                    return@exchange runCatching {
                        response.bodyTo(object :
                            ParameterizedTypeReference<Map<String, String>>() {})?.get("id")
                    }.getOrElse {
                        throw IllegalStateException(
                            "Create persona success but, cannot get persona-id", it
                        )
                    } ?: throw IllegalStateException(
                        "Create persona success but, cannot get persona-id cause it's null"
                    )
                }
                throw IllegalArgumentException("Cannot add persona name \"$personaName\" to user")
            }
    }

    override fun deletePersona(token: String, personaId: String) {
        restClient.delete()
            .uri("/internals/personas?persona-id=$personaId")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange { _, response ->
                require(response.statusCode.is2xxSuccessful) { "Cannot delete persona by personaId \"$personaId\"" }
            }
    }
}
