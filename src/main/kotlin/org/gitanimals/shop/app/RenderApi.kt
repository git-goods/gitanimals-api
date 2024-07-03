package org.gitanimals.shop.app

interface RenderApi {

    fun getPersonaById(token: String, personaId: Long): PersonaResponse

    fun deletePersonaById(token: String, personaId: Long)

    fun addPersona(
        token: String,
        idempotencyKey: String,
        personaId: Long,
        personaLevel: Int,
        personaType: String,
    )

    data class PersonaResponse(
        val id: String,
        val type: String,
        val level: String,
    )
}
