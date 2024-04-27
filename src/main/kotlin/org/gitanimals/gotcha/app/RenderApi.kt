package org.gitanimals.gotcha.app

interface RenderApi {

    fun addPersona(token: String, idempotencyKey: String, personaName: String): String

    fun deletePersona(token: String, personaId: String)
}
