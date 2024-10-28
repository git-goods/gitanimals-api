package org.gitanimals.shop.app

interface RenderApi {

    fun getPersonaById(token: String, personaId: Long): PersonaResponse

    fun addBackground(token: String, idempotencyKey: String, backgroundName: String)

    fun deleteBackground(token: String, idempotencyKey: String, backgroundName: String)

    fun deletePersonaById(token: String, personaId: Long)

    fun addPersona(
        token: String,
        idempotencyKey: String,
        personaId: Long,
        personaLevel: Int,
        personaType: String,
    )

    fun addPersonas(
        token: String,
        idempotencyKeys: List<String>,
        personaNames: List<String>,
    ): List<String>

    data class PersonaResponse(
        val id: String,
        val type: String,
        val level: String,
    )
}
