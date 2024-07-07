package org.gitanimals.gotcha.infra

import org.gitanimals.gotcha.app.RenderApi
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class RestRenderApi(
    @Qualifier("renderRestClient") private val restClient: RestClient,
    @Value("\${internal.secret}") private val internalSecret: String,
) : RenderApi {

    override fun addPersonas(
        token: String,
        idempotencyKeys: List<String>,
        personaNames: List<String>
    ): List<String> {
        val request = mutableListOf<AddPersonaRequest>()
        for (nameWithIndex in personaNames.withIndex()) {
            request.add(
                AddPersonaRequest(
                    idempotencyKeys[nameWithIndex.index], nameWithIndex.value,
                )
            )
        }

        return restClient.post()
            .uri("/internals/personas/multiply")
            .header(HttpHeaders.AUTHORIZATION, token)
            .header("Internal-Secret", internalSecret)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange { _, response ->
                if (response.statusCode.is2xxSuccessful) {
                    return@exchange runCatching {
                        val personaResponses = response.bodyTo(object :
                            ParameterizedTypeReference<List<PersonaResponse>>() {})

                        personaResponses?.map { it.id }
                    }.getOrElse {
                        throw IllegalStateException(
                            "Create persona success but, cannot get persona-id", it
                        )
                    } ?: throw IllegalStateException(
                        "Create persona success but, cannot get persona-id cause it's null"
                    )
                }
                throw IllegalArgumentException("Cannot add persona \"$personaNames\" to user")
            }
    }

    override fun deletePersona(token: String, personaId: String) {
        restClient.delete()
            .uri("/internals/personas?persona-id=$personaId")
            .header(HttpHeaders.AUTHORIZATION, token)
            .header("Internal-Secret", internalSecret)
            .exchange { _, response ->
                require(response.statusCode.is2xxSuccessful) { "Cannot delete persona by personaId \"$personaId\"" }
            }
    }

    data class AddPersonaRequest(
        val idempotencyKey: String,
        val personaName: String,
    )

    data class PersonaResponse(
        val id: String,
        val type: String,
        val level: String,
        val visible: Boolean,
        val dropRate: String,
    )
}
