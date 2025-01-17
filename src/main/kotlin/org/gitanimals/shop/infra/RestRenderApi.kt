package org.gitanimals.shop.infra

import org.gitanimals.core.filter.MDCFilter
import org.gitanimals.shop.app.RenderApi
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component("shop.RestRenderApi")
class RestRenderApi(
    @Qualifier("shop.renderRestClient") private val restClient: RestClient,
    @Value("\${internal.secret}") private val internalSecret: String,
) : RenderApi {

    override fun getPersonaById(token: String, personaId: Long): RenderApi.PersonaResponse {
        return restClient.get()
            .uri("/personas/$personaId")
            .header(HttpHeaders.AUTHORIZATION, token)
            .header(MDCFilter.TRACE_ID, MDC.get(MDCFilter.TRACE_ID))
            .exchange { _, response ->
                runCatching {
                    response.bodyTo(RenderApi.PersonaResponse::class.java)
                }.getOrElse {
                    require(!response.statusCode.is4xxClientError) {
                        "Cannot get persona by personaId \"$personaId\""
                    }

                    throw IllegalStateException(it)
                }
            }
    }

    override fun deletePersonaById(token: String, personaId: Long) {
        return restClient.delete()
            .uri("/internals/personas?persona-id=$personaId")
            .header(HttpHeaders.AUTHORIZATION, token)
            .header(MDCFilter.TRACE_ID, MDC.get(MDCFilter.TRACE_ID))
            .header("Internal-Secret", internalSecret)
            .exchange { _, response ->
                require(response.statusCode.is2xxSuccessful) { "Cannot delete persona by personaId \"$personaId\"" }
            }
    }

    override fun addBackground(token: String, idempotencyKey: String, backgroundName: String) {
        return restClient.post()
            .uri("/internals/backgrounds?name=$backgroundName")
            .header(HttpHeaders.AUTHORIZATION, token)
            .header(MDCFilter.TRACE_ID, MDC.get(MDCFilter.TRACE_ID))
            .header("Internal-Secret", internalSecret)
            .exchange { _, response ->
                if (response.statusCode.is4xxClientError) {
                    response.bodyTo(ErrorResponse::class.java)?.let {
                        throw IllegalArgumentException(it.message)
                    }
                }
                check(response.statusCode.is2xxSuccessful) { "Cannot add background by backgroundName: \"$backgroundName\"" }
            }
    }

    override fun deleteBackground(token: String, idempotencyKey: String, backgroundName: String) {
        return restClient.delete()
            .uri("/internals/backgrounds?name=$backgroundName")
            .header(HttpHeaders.AUTHORIZATION, token)
            .header(MDCFilter.TRACE_ID, MDC.get(MDCFilter.TRACE_ID))
            .header("Internal-Secret", internalSecret)
            .exchange { _, response ->
                require(response.statusCode.is2xxSuccessful) { "Cannot delete background by backgroundName: \"$backgroundName\"" }
            }
    }

    override fun addPersona(
        token: String,
        idempotencyKey: String,
        personaId: Long,
        personaLevel: Int,
        personaType: String
    ) {
        return restClient.post()
            .uri("/internals/personas?idempotency-key=$idempotencyKey")
            .header(HttpHeaders.AUTHORIZATION, token)
            .header(MDCFilter.TRACE_ID, MDC.get(MDCFilter.TRACE_ID))
            .header("Internal-Secret", internalSecret)
            .body(AddPersonaRequest(personaId, personaType, personaLevel))
            .exchange { _, response ->
                require(response.statusCode.is2xxSuccessful) {
                    "Cannot add persona \"$personaId\", \"$personaLevel\", \"$personaType\" to user"
                }
            }
    }

    override fun addPersonas(
        token: String,
        idempotencyKeys: List<String>,
        personaNames: List<String>
    ): List<String> {
        val request = mutableListOf<AddMultiplePersonaRequest>()
        for (nameWithIndex in personaNames.withIndex()) {
            request.add(
                AddMultiplePersonaRequest(
                    idempotencyKeys[nameWithIndex.index], nameWithIndex.value,
                )
            )
        }

        return restClient.post()
            .uri("/internals/personas/multiply")
            .header(HttpHeaders.AUTHORIZATION, token)
            .header(MDCFilter.TRACE_ID, MDC.get(MDCFilter.TRACE_ID))
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

    private data class AddPersonaRequest(
        val id: Long,
        val name: String,
        val level: Int,
    )

    private data class AddMultiplePersonaRequest(
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

    data class ErrorResponse(
        val message: String,
    )
}
