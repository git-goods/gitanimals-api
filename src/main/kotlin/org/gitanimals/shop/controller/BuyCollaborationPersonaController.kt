package org.gitanimals.shop.controller

import org.gitanimals.shop.app.BuyCollaborationPersonaFacade
import org.gitanimals.shop.app.request.BuyCollaborationPersonaRequest
import org.gitanimals.shop.controller.response.CollaborationPersona
import org.gitanimals.shop.domain.CollaborationPersonaService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class BuyCollaborationPersonaController(
    private val buyCollaborationPersonaFacade: BuyCollaborationPersonaFacade,
    private val collaborationPersonaService: CollaborationPersonaService,
) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("shops/personas/collaborations")
    fun buyCollaborationPersona(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestBody request: BuyCollaborationPersonaRequest,
    ) = buyCollaborationPersonaFacade.buyCollaborationPet(token, request)

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("shops/personas/collaborations")
    fun findAllCollaborationPersonas(): List<CollaborationPersona> =
        collaborationPersonaService.findAllCollaborationPersonas()
            .map { CollaborationPersona(it.name, it.price.toString(), it.description) }
}
