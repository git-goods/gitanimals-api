package org.gitanimals.shop.controller.response

import org.gitanimals.shop.domain.DropPersona

data class DropPersonaResponse(
    val id: String,
    val personaId: String,
    val droppedUserId: String,
    val givenPoint: Long,
) {

    companion object {
        fun from(dropPersona: DropPersona): DropPersonaResponse {
            return DropPersonaResponse(
                id = dropPersona.id.toString(),
                personaId = dropPersona.personaId.toString(),
                droppedUserId = dropPersona.droppedUserId.toString(),
                givenPoint = dropPersona.givenPoint,
            )
        }
    }
}
