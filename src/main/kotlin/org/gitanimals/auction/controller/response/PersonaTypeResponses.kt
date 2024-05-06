package org.gitanimals.auction.controller.response

import org.gitanimals.auction.domain.PersonaType

data class PersonaTypeResponses(
    val productTypes: List<PersonaTypeResponse>
) {

    companion object {
        fun from(personaTypes: Array<PersonaType>): PersonaTypeResponses
            = PersonaTypeResponses(personaTypes.map { PersonaTypeResponse.from(it) })
    }
}
