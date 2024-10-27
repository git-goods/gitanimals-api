package org.gitanimals.shop.app

import org.gitanimals.shop.domain.SaleService
import org.gitanimals.shop.domain.SaleType
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class BuyBackgroundFacade(
    orchestratorFactory: OrchestratorFactory,
    identityApi: IdentityApi,
    renderApi: RenderApi,

    private val saleService: SaleService,
) {

    private lateinit var backgroundBuyOrchestrator: Orchestrator<String, Unit>

    fun buyBackground(token: String, item: String) {
        backgroundBuyOrchestrator.sagaSync(
            item,
            mapOf("token" to token, "idempotencyKey" to UUID.randomUUID().toString())
        ).decodeResultOrThrow(Unit::class)
    }

    init {
        backgroundBuyOrchestrator = orchestratorFactory.create<String>("buy background facade")
            .start({
                val sale = saleService.getByTypeAndItem(SaleType.BACKGROUND, it)

                require(sale.getCount() > 0) {
                    "Cannot buy item : \"${sale.type}\" cause its count : \"${sale.getCount()}\" == 0"
                }

                sale
            })
            .joinWithContext(
                contextOrchestrate = { context, sale ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)
                    identityApi.decreasePoint(token, idempotencyKey, sale.price.toString())

                    sale
                },
                contextRollback = { context, sale ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)
                    identityApi.increasePoint(token, idempotencyKey, sale.price.toString())
                }
            )
            .joinWithContext(
                contextOrchestrate = { context, sale ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    renderApi.addBackground(token, idempotencyKey, sale.item)
                    sale
                },
                contextRollback = { context, sale ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    renderApi.deleteBackground(token, idempotencyKey, sale.item)
                }
            )
            .commit { sale ->
                saleService.buyBySaleTypeAndItem(sale.type, sale.item)
            }
    }
}
