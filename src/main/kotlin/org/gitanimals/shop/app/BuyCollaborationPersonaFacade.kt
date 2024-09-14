package org.gitanimals.shop.app

import org.gitanimals.shop.app.request.BuyCollaborationPersonaRequest
import org.gitanimals.shop.domain.CollaborationPersonaService
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class BuyCollaborationPersonaFacade(
    orchestratorFactory: OrchestratorFactory,
    val renderApi: RenderApi,
    val identityApi: IdentityApi,
    private val collaborationPersonaService: CollaborationPersonaService,
) {

    private lateinit var buyCollaborationPersonaOrchestrator: Orchestrator<BuyCollaborationPersonaRequest, Unit>

    fun buyCollaborationPet(token: String, request: BuyCollaborationPersonaRequest) {
        val response = buyCollaborationPersonaOrchestrator.sagaSync(
            request,
            context = mapOf("token" to token, "idempotencyKey" to UUID.randomUUID().toString())
        )

        if (response.isSuccess.not()) {
            response.throwError()
        }
    }

    init {
        buyCollaborationPersonaOrchestrator =
            orchestratorFactory.create<BuyCollaborationPersonaRequest>("buy collaboration persona orchestrator")
                .start(orchestrate = { collaborationPersonaService.getCollaborationPersonaByName(it.name) })
                .joinWithContext(
                    contextOrchestrate = { context, request ->
                        val token = context.decodeContext("token", String::class)
                        val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                        identityApi.decreasePoint(token, idempotencyKey, request.price.toString())
                        request
                    },
                    contextRollback = { context, request ->
                        val token = context.decodeContext("token", String::class)
                        val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                        identityApi.increasePoint(token, idempotencyKey, request.price.toString())
                        request
                    }
                )
                .commitWithContext { context, request ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    val name = collaborationPersonaService.getCollaborationPersonaByName(request.name).name

                    renderApi.addPersonas(token, listOf(idempotencyKey), listOf(name))
                }
    }
}
