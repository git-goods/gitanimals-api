package org.gitanimals.auction.controller.response

import org.gitanimals.auction.domain.PersonaType

data class PersonaTypeResponse(
    val name: String,
) {

    companion object {
        fun from(personaType: PersonaType) =
            PersonaTypeResponse(personaType.name)
    }
}
