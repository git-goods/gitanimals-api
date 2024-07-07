package org.gitanimals.shop.controller

import org.gitanimals.shop.app.DropPersonaFacade
import org.gitanimals.shop.domain.DropPersona
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class DropPersonaController(
    private val dropPersonaFacade: DropPersonaFacade,
) {

    @PostMapping("shops/drop/{persona-id}")
    fun dropPersona(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("persona-id") personaId: Long,
    ): DropPersona = dropPersonaFacade.dropPersona(token, personaId)
}
