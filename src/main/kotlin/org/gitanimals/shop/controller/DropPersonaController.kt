package org.gitanimals.shop.controller

import org.gitanimals.core.auth.RequiredUserEntryPoints
import org.gitanimals.core.auth.UserEntryPoint
import org.gitanimals.shop.app.DropPersonaFacade
import org.gitanimals.shop.controller.response.DropPersonaResponse
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
    @RequiredUserEntryPoints([UserEntryPoint.GITHUB])
    fun dropPersona(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("persona-id") personaId: Long,
    ): DropPersonaResponse =
        DropPersonaResponse.from(dropPersonaFacade.dropPersona(token, personaId))
}
