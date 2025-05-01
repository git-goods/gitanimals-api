package org.gitanimals.shop.app

import org.gitanimals.core.TraceIdContextOrchestrator
import org.gitanimals.core.TraceIdContextRollback
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ENTRY_POINT
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ID
import org.gitanimals.shop.app.request.BuyCollaborationPersonaRequest
import org.gitanimals.shop.domain.CollaborationPersonaService
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import java.util.*

@Service
class BuyCollaborationPersonaFacade(
    orchestratorFactory: OrchestratorFactory,
    val renderApi: RenderApi,
    val identityApi: IdentityApi,
    private val collaborationPersonaService: CollaborationPersonaService,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)
    private lateinit var buyCollaborationPersonaOrchestrator: Orchestrator<BuyCollaborationPersonaRequest, Unit>

    fun buyCollaborationPet(token: String, request: BuyCollaborationPersonaRequest) {
        val response = buyCollaborationPersonaOrchestrator.sagaSync(
            request,
            context = mapOf(
                "token" to token,
                "idempotencyKey" to UUID.randomUUID().toString(),
                USER_ID to MDC.get(USER_ID),
                TRACE_ID to MDC.get(TRACE_ID),
                USER_ENTRY_POINT to MDC.get(USER_ENTRY_POINT),
            )
        )

        if (response.isSuccess.not()) {
            response.throwError()
        }
    }

    init {
        buyCollaborationPersonaOrchestrator =
            orchestratorFactory.create<BuyCollaborationPersonaRequest>("buy collaboration persona orchestrator")
                .startWithContext(contextOrchestrate = TraceIdContextOrchestrator { _, request ->
                    collaborationPersonaService.getCollaborationPersonaByName(request.name)
                })
                .joinWithContext(
                    contextOrchestrate = TraceIdContextOrchestrator { context, request ->
                        val token = context.decodeContext("token", String::class)
                        val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                        identityApi.decreasePoint(token, idempotencyKey, request.price.toString())
                        request
                    },
                    contextRollback = TraceIdContextRollback { context, request ->
                        val token = context.decodeContext("token", String::class)
                        val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                        logger.warn("Cannot buy collboration persona rollback buy point...")
                        identityApi.increasePoint(token, idempotencyKey, request.price.toString())
                        logger.warn("Cannot buy collboration persona rollback buy point succcess")
                        request
                    }
                )
                .commitWithContext(TraceIdContextOrchestrator { context, request ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    val name =
                        collaborationPersonaService.getCollaborationPersonaByName(request.name).name

                    renderApi.addPersonas(
                        token = token,
                        addMultiplePersonaRequest = listOf(
                            RenderApi.AddMultiplePersonaRequest(
                                idempotencyKey = idempotencyKey,
                                personaName = name,
                            )
                        ),
                    )
                })
    }
}
