package org.gitanimals.shop.app

import org.gitanimals.shop.domain.DropPersona
import org.gitanimals.shop.domain.DropPersonaService
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class DropPersonaFacade(
    orchestratorFactory: OrchestratorFactory,
    identityApi: IdentityApi,
    renderApi: RenderApi,

    private val dropPersonaService: DropPersonaService,
) {

    private val orchestrator: Orchestrator<Long, DropPersona> =
        orchestratorFactory.create<Long>("drop persona orchestrator")
            .startWithContext(
                contextOrchestrate = { context, personaId ->
                    val token = context.decodeContext("token", String::class)
                    context.set("persona", renderApi.getPersonaById(token, personaId))

                    renderApi.deletePersonaById(token, personaId)
                    personaId
                },
                contextRollback = { context, _ ->
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)
                    val token = context.decodeContext("token", String::class)
                    val persona = context.decodeContext("persona", RenderApi.PersonaResponse::class)

                    renderApi.addPersona(
                        token,
                        idempotencyKey,
                        persona.id.toLong(),
                        persona.level.toInt(),
                        persona.type
                    )
                }
            )
            .joinWithContext(
                contextOrchestrate = { context, personId ->
                    val token = context.decodeContext("token", String::class)
                    val seller = identityApi.getUserByToken(token)

                    val dropPersona = dropPersonaService.dropPersona(personId, seller.id.toLong())
                    context.set("dropPersonaId", dropPersona.id)
                    dropPersona
                },
                contextRollback = { context, _ ->
                    dropPersonaService.deleteDropPersona(context.decodeContext("dropPersonaId", Long::class))
                }
            )
            .commitWithContext { context, dropPersona ->
                val token = context.decodeContext("token", String::class)
                val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                identityApi.increasePoint(token, idempotencyKey, dropPersona.givenPoint.toString())
                dropPersona
            }

    fun dropPersona(token: String, personaId: Long): DropPersona {
        return orchestrator.sagaSync(
            personaId,
            mapOf("token" to token, "idempotencyKey" to UUID.randomUUID().toString())
        ).decodeResultOrThrow(DropPersona::class)
    }

}
