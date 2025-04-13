package org.gitanimals.gotcha.app

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.DeleteExchange
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

interface RenderApi {

    @PostExchange("/internals/personas/multiply")
    fun addPersonas(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestBody addPersonaRequest: List<AddPersonaRequest>,
    ): List<PersonaResponse>

    @DeleteExchange("/internals/personas")
    fun deletePersona(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam("persona-id") personaId: String,
    )

    @GetExchange("/personas/infos")
    fun getAllPersonas(): PersonaWithDropRateResponse

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

    data class PersonaWithDropRateResponse(
        val personas: List<PersonaResponse>,
    ) {
        data class PersonaResponse(
            val type: String,
            val dropRate: String,
        )
    }
}
