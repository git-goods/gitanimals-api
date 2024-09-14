package org.gitanimals.shop.domain

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CollaborationPersonaService(
    private val collaborationPersonaRepository: CollaborationPersonaRepository,
) {

    fun getCollaborationPersonaByName(name: String): CollaborationPersona {
        return collaborationPersonaRepository.findByIdOrNull(name)
            ?: throw IllegalArgumentException("Cannto find matched collaboration pet by name \"$name\"")
    }

    fun findAllCollaborationPersonas(): List<CollaborationPersona> =
        collaborationPersonaRepository.findAll()
}
