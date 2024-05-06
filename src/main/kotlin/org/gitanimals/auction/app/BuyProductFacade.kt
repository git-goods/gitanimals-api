package org.gitanimals.auction.app

import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductService
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class BuyProductFacade(
    private val renderApi: RenderApi,
    private val identityApi: IdentityApi,
    private val productService: ProductService,
    orchestratorFactory: OrchestratorFactory,
) {

    private lateinit var orchestrator: Orchestrator<Long, Product>

    fun buyProduct(token: String, productId: Long): Product {
        return orchestrator.sagaSync(
            productId,
            mapOf(
                "token" to token,
                "idempotencyKey" to UUID.randomUUID().toString()
            )
        ).decodeResultOrThrow(Product::class)
    }

    init {
        this.orchestrator = orchestratorFactory.create<Long>("buy product orchestrator")
            .startWithContext(
                contextOrchestrate = { context, productId ->
                    val buyer = identityApi.getUserByToken(context.decodeContext("token", String::class))
                    val product = productService.getProductById(productId)

                    require(product.getPrice() <= buyer.points.toLong()) {
                        "Cannot buy product cause buyer does not have enough point \"${product.getPrice()}\" >= \"${buyer.points}\""
                    }

                    productService.waitBuyProduct(productId, buyer.id.toLong())
                },
                contextRollback = { _, productId -> productService.rollbackProduct(productId) }
            )
            .joinWithContext(
                contextOrchestrate = { context, product ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    identityApi.decreasePoint(token, idempotencyKey, product.getPrice().toString())

                    product
                },
                contextRollback = { context, product ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    identityApi.increasePoint(token, idempotencyKey, product.getPrice().toString())
                }
            )
            .joinWithContext(
                contextOrchestrate = { context, product ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    renderApi.addPersona(
                        token,
                        idempotencyKey,
                        product.persona.personaId,
                        product.persona.personaLevel,
                        product.persona.personaType,
                    )

                    product
                },
                contextRollback = { context, product ->
                    val token = context.decodeContext("token", String::class)

                    renderApi.deletePersonaById(token, product.persona.personaId)
                }
            ).commit { product -> productService.buyProduct(product.id) }
    }
}
