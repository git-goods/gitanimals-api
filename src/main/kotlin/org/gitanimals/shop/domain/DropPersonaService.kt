package org.gitanimals.shop.domain

import org.springframework.stereotype.Service

@Service
class DropPersonaService(
    private val dropPersonaRepository: DropPersonaRepository,
) {

    fun dropPersona(personaId: Long, userId: Long): DropPersona {
        val dropPersona = DropPersona.of(personaId, userId)

        return dropPersonaRepository.save(dropPersona)
    }

    fun deleteDropPersona(id: Long) = dropPersonaRepository.deleteById(id)
}
