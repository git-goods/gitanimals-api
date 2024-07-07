package org.gitanimals.gotcha.app

interface RenderApi {

    fun addPersonas(
        token: String,
        idempotencyKeys: List<String>,
        personaNames: List<String>,
    ): List<String>

    fun deletePersona(token: String, personaId: String)

    data class AddPersonaResponse(
        val id: String,
        val idempotencyKey: String,
    )
}
