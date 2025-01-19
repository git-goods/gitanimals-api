package org.gitanimals.auction.app

import org.gitanimals.auction.domain.ProductService
import org.gitanimals.core.TraceIdContextOrchestrator
import org.gitanimals.core.TraceIdContextRollback
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeleteProductFacade(
    private val renderApi: RenderApi,
    private val identityApi: IdentityApi,
    private val productService: ProductService,
    orchestratorFactory: OrchestratorFactory,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)
    private lateinit var orchestrator: Orchestrator<Long, Long>

    fun deleteProduct(token: String, productId: Long): Long {
        return orchestrator.sagaSync(
            productId,
            mapOf(
                "token" to token,
                "idempotencyKey" to UUID.randomUUID().toString(),
                TRACE_ID to MDC.get(TRACE_ID),
            ),
        ).decodeResultOrThrow(Long::class)
    }

    init {
        this.orchestrator = orchestratorFactory.create<Long>("delete product orchestrator")
            .startWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, productId ->
                    val token = context.decodeContext("token", String::class)
                    val sellerId = identityApi.getUserByToken(token).id.toLong()

                    context.set("sellerId", sellerId)

                    productService.waitDeleteProduct(sellerId, productId)
                },
                contextRollback = TraceIdContextRollback { _, productId ->
                    logger.warn("Cannot delete product from auction rollback delete product...")
                    productService.rollbackProduct(productId)
                    logger.warn("Cannot delete product from auction rollback delete product success")
                }
            )
            .joinWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, product ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)
                    renderApi.addPersona(
                        token,
                        idempotencyKey,
                        product.persona.personaId,
                        product.persona.personaLevel,
                        product.persona.personaType
                    )
                    product.id
                },
                contextRollback = TraceIdContextRollback { context, product ->
                    val token = context.decodeContext("token", String::class)

                    logger.warn("Cannot delete product from auction rollback add persona...")
                    renderApi.deletePersonaById(token, product.persona.personaId)
                    logger.warn("Cannot delete product from auction rollback add persona success")
                }
            )
            .commitWithContext(TraceIdContextOrchestrator { context, productId ->
                val sellerId = context.decodeContext("sellerId", Long::class)
                productService.deleteProduct(sellerId, productId)
            })
    }
}
