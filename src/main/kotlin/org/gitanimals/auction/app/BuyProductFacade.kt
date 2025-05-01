package org.gitanimals.auction.app

import org.gitanimals.auction.app.RenderApi.AddPersonaRequest
import org.gitanimals.auction.app.event.InboxInputEvent
import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductService
import org.gitanimals.core.TraceIdContextOrchestrator
import org.gitanimals.core.TraceIdContextRollback
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ENTRY_POINT
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ID
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.rooftop.netx.api.SagaManager
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import java.util.*

@Service
class BuyProductFacade(
    private val renderApi: RenderApi,
    private val identityApi: IdentityApi,
    private val productService: ProductService,
    private val sagaManager: SagaManager,
    orchestratorFactory: OrchestratorFactory,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)
    private lateinit var orchestrator: Orchestrator<Long, Product>

    fun buyProduct(token: String, productId: Long): Product {
        val result = orchestrator.sagaSync(
            productId,
            mapOf(
                "token" to token,
                "idempotencyKey" to UUID.randomUUID().toString(),
                TRACE_ID to MDC.get(TRACE_ID),
                USER_ID to MDC.get(USER_ID),
                USER_ENTRY_POINT to MDC.get(USER_ENTRY_POINT),
            )
        ).decodeResultOrThrow(Product::class)

        publishSoldOutEvent(result)

        return result
    }

    private fun publishSoldOutEvent(product: Product) {
        val user = identityApi.getUserById(product.sellerId)

        sagaManager.startSync(InboxInputEvent.createSoldOutInbox(user.username, product))
    }

    init {
        this.orchestrator = orchestratorFactory.create<Long>("buy product orchestrator")
            .startWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, productId ->
                    val buyer =
                        identityApi.getUserByToken(context.decodeContext("token", String::class))
                    val product = productService.getProductById(productId)

                    require(product.getPrice() <= buyer.points.toLong()) {
                        "Cannot buy product cause buyer does not have enough point \"${product.getPrice()}\" >= \"${buyer.points}\""
                    }

                    productService.waitBuyProduct(productId, buyer.id.toLong())
                },
                contextRollback = TraceIdContextRollback { _, productId ->
                    logger.warn("Fail to buy product rollback product status... \"$productId\"")
                    productService.rollbackProduct(productId)
                    logger.warn("Fail to buy product rollback product status success... \"$productId\"")
                }
            )
            .joinWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, product ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    identityApi.decreasePoint(token, idempotencyKey, product.getPrice().toString())

                    product
                },
                contextRollback = TraceIdContextRollback { context, product ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    logger.warn("Fail to buy product increase point...")
                    identityApi.increasePoint(token, idempotencyKey, product.getPrice().toString())
                    logger.warn("Fail to buy product increase point success")
                }
            )
            .joinWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, product ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    renderApi.addPersona(
                        token = token,
                        idempotencyKey = idempotencyKey,
                        request = AddPersonaRequest(
                            product.persona.personaId,
                            product.persona.personaType,
                            product.persona.personaLevel,
                        )
                    )

                    product
                },
                contextRollback = TraceIdContextRollback { context, product ->
                    val token = context.decodeContext("token", String::class)

                    logger.warn("Fail to buy product delete persona to buyer...")
                    renderApi.deletePersonaById(token, product.persona.personaId)
                    logger.warn("Fail to buy product delete persona to buyer success")
                }
            )
            .joinWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, product ->
                    val sellerId = product.sellerId
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    identityApi.increasePointById(
                        sellerId,
                        idempotencyKey,
                        product.getPrice().toString()
                    )

                    product
                },
                contextRollback = TraceIdContextRollback { context, product ->
                    val sellerId = product.sellerId
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    logger.warn("Fail to buy product decrease point to seller...")
                    identityApi.decreasePointById(
                        sellerId,
                        idempotencyKey,
                        product.getPrice().toString()
                    )
                    logger.warn("Fail to buy product decrease point to seller success")
                }
            )
            .commitWithContext(TraceIdContextOrchestrator { _, product ->
                productService.buyProduct(product.id)
            })
    }
}
