package org.gitanimals.shop.app

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.DeleteExchange
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

interface RenderApi {

    @GetExchange("/personas/{personaId}")
    fun getPersonaById(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("personaId") personaId: Long,
    ): PersonaResponse

    @PostExchange("/internals/backgrounds")
    fun addBackground(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("name") backgroundName: String,
    )

    @DeleteExchange("/internals/backgrounds")
    fun deleteBackground(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("name") backgroundName: String,
    )

    @DeleteExchange("/internals/personas")
    fun deletePersonaById(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("persona-id") personaId: Long,
    )

    @PostExchange("/internals/personas")
    fun addPersona(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("idempotency-key") idempotencyKey: String,
        @RequestBody addPersonaRequest: AddPersonaRequest,
    )

    @PostExchange("/internals/personas/multiply")
    fun addPersonas(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestBody addMultiplePersonaRequest: List<AddMultiplePersonaRequest>,
    ): List<String>

    data class AddPersonaRequest(
        val id: Long,
        val name: String,
        val level: Int,
    )

    data class AddMultiplePersonaRequest(
        val idempotencyKey: String,
        val personaName: String,
    )

    data class PersonaResponse(
        val id: String,
        val type: String,
        val level: String,
    )
}
