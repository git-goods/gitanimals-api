package org.gitanimals.auction.app

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

    @DeleteExchange("/internals/personas")
    fun deletePersonaById(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("persona-id") personaId: Long,
    )

    @PostExchange("/internals/personas")
    fun addPersona(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("idempotency-key") idempotencyKey: String,
        @RequestBody request: AddPersonaRequest
    )

    data class AddPersonaRequest(
        val id: Long,
        val name: String,
        val level: Int,
    )

    data class PersonaResponse(
        val id: String,
        val type: String,
        val level: String,
    )
}
