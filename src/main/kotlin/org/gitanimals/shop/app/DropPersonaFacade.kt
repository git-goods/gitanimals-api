package org.gitanimals.shop.app

import org.gitanimals.core.TraceIdContextOrchestrator
import org.gitanimals.core.TraceIdContextRollback
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ENTRY_POINT
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ID
import org.gitanimals.shop.domain.DropPersona
import org.gitanimals.shop.domain.DropPersonaService
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import java.util.*

@Service
class DropPersonaFacade(
    orchestratorFactory: OrchestratorFactory,
    identityApi: IdentityApi,
    renderApi: RenderApi,

    private val dropPersonaService: DropPersonaService,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)
    private val orchestrator: Orchestrator<Long, DropPersona> =
        orchestratorFactory.create<Long>("drop persona orchestrator")
            .startWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, personaId ->
                    val token = context.decodeContext("token", String::class)
                    context.set("persona", renderApi.getPersonaById(token, personaId))

                    renderApi.deletePersonaById(token, personaId)
                    personaId
                },
                contextRollback = TraceIdContextRollback { context, _ ->
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)
                    val token = context.decodeContext("token", String::class)
                    val persona = context.decodeContext("persona", RenderApi.PersonaResponse::class)

                    logger.warn("Cannot drop persona rollback drop persona data...")
                    renderApi.addPersona(
                        token,
                        idempotencyKey,
                        addPersonaRequest = RenderApi.AddPersonaRequest(
                            id = persona.id.toLong(),
                            name = persona.type,
                            level = persona.level.toInt(),
                        )
                    )
                    logger.warn("Cannot drop persona rollback drop persona success")
                }
            )
            .joinWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, personId ->
                    val token = context.decodeContext("token", String::class)
                    val seller = identityApi.getUserByToken(token)

                    val dropPersona = dropPersonaService.dropPersona(personId, seller.id.toLong())
                    context.set("dropPersonaId", dropPersona.id)
                    dropPersona
                },
                contextRollback = TraceIdContextRollback { context, _ ->

                    logger.warn("Cannot drop persona rollback drop persona data...")
                    dropPersonaService.deleteDropPersona(
                        context.decodeContext(
                            "dropPersonaId",
                            Long::class
                        )
                    )
                    logger.warn("Cannot drop persona rollback drop persona success")
                }
            )
            .commitWithContext(TraceIdContextOrchestrator { context, dropPersona ->
                val token = context.decodeContext("token", String::class)
                val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                identityApi.increasePoint(token, idempotencyKey, dropPersona.givenPoint.toString())
                dropPersona
            })

    fun dropPersona(token: String, personaId: Long): DropPersona {
        return orchestrator.sagaSync(
            personaId,
            mapOf(
                "token" to token,
                "idempotencyKey" to UUID.randomUUID().toString(),
                TRACE_ID to MDC.get(TRACE_ID),
                USER_ID to MDC.get(USER_ID),
                USER_ENTRY_POINT to MDC.get(USER_ENTRY_POINT),
            )
        ).decodeResultOrThrow(DropPersona::class)
    }

}
