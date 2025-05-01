package org.gitanimals.auction.app

import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductService
import org.gitanimals.auction.domain.request.RegisterProductRequest
import org.gitanimals.core.TraceIdContextOrchestrator
import org.gitanimals.core.TraceIdContextRollback
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ENTRY_POINT
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ID
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import java.util.*

@Service
class RegisterProductFacade(
    private val identityApi: IdentityApi,
    private val renderApi: RenderApi,
    private val productService: ProductService,
    orchestratorFactory: OrchestratorFactory,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)
    private lateinit var orchestrator: Orchestrator<RegisterProductRequest, Product>

    fun registerProduct(token: String, personaId: Long, price: Long): Product {
        val seller = identityApi.getUserByToken(token)
        val sellProduct = renderApi.getPersonaById(token, personaId)

        val request = RegisterProductRequest(
            sellerId = seller.id.toLong(),
            personaId = sellProduct.id.toLong(),
            personaType = sellProduct.type,
            personaLevel = sellProduct.level.toInt(),
            price = price,
        )

        return orchestrator.sagaSync(
            100000,
            request,
            mapOf(
                "token" to token,
                "idempotencyKey" to UUID.randomUUID().toString(),
                TRACE_ID to MDC.get(TRACE_ID),
                USER_ID to MDC.get(USER_ID),
                USER_ENTRY_POINT to MDC.get(USER_ENTRY_POINT),
            ),
        ).decodeResultOrThrow(Product::class)
    }

    init {
        this.orchestrator =
            orchestratorFactory.create<RegisterProductRequest>("register product orchestrator")
                .startWithContext(
                    contextOrchestrate = TraceIdContextOrchestrator { context, request ->
                        val token = context.decodeContext("token", String::class)
                        renderApi.deletePersonaById(token, request.personaId)
                        request
                    },
                    contextRollback = TraceIdContextRollback { context, request ->
                        val token = context.decodeContext("token", String::class)
                        val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                        logger.warn("Cannot register product rollback delete persona...")
                        renderApi.addPersona(
                            token = token,
                            idempotencyKey = idempotencyKey,
                            request = RenderApi.AddPersonaRequest(
                                id = request.personaId,
                                name = request.personaType,
                                level = request.personaLevel,
                            ),
                        )
                        logger.warn("Cannot register product rollback delete persona success")
                    }
                )
                .commitWithContext(TraceIdContextOrchestrator { _, request ->
                    productService.registerProduct(request)
                })
    }
}
