package org.gitanimals.auction.app

import org.gitanimals.auction.domain.ProductService
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeleteProductFacade(
    private val renderApi: RenderApi,
    private val identityApi: IdentityApi,
    private val productService: ProductService,
    orchestratorFactory: OrchestratorFactory,
) {

    private lateinit var orchestrator: Orchestrator<Long, Long>

    fun deleteProduct(token: String, productId: Long): Long {
        return orchestrator.sagaSync(
            productId,
            mapOf("token" to token, "idempotencyKey" to UUID.randomUUID().toString()),
        ).decodeResultOrThrow(Long::class)
    }

    init {
        this.orchestrator = orchestratorFactory.create<Long>("delete product orchestrator")
            .startWithContext(
                contextOrchestrate = { context, productId ->
                    val token = context.decodeContext("token", String::class)
                    val sellerId = identityApi.getUserByToken(token).id.toLong()

                    context.set("sellerId", sellerId)

                    productService.waitDeleteProduct(sellerId, productId)
                },
                contextRollback = { _, productId -> productService.rollbackProduct(productId) }
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
                        product.persona.personaType
                    )
                    product.id
                },
                contextRollback = { context, product ->
                    val token = context.decodeContext("token", String::class)
                    renderApi.deletePersonaById(token, product.persona.personaId)
                }
            )
            .commitWithContext { context, productId ->
                val sellerId = context.decodeContext("sellerId", Long::class)
                productService.deleteProduct(sellerId, productId)
            }
    }
}
