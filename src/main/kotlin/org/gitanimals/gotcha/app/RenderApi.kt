package org.gitanimals.gotcha.app

interface RenderApi {

    fun addPersonas(
        token: String,
        idempotencyKeys: List<String>,
        personaNames: List<String>,
    ): List<String>

    fun deletePersona(token: String, personaId: String)

    fun getAllPersonas(): PersonaWithDropRateResponse

    data class AddPersonaResponse(
        val id: String,
        val idempotencyKey: String,
    )

    data class PersonaWithDropRateResponse(
        val personas: List<PersonaResponse>,
    ) {
        data class PersonaResponse(
            val type: String,
            val dropRate: String,
        )
    }
}
