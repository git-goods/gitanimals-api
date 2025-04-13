package org.gitanimals.shop.app

import org.gitanimals.core.TraceIdContextOrchestrator
import org.gitanimals.core.TraceIdContextRollback
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ID
import org.gitanimals.shop.domain.SaleService
import org.gitanimals.shop.domain.SaleType
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import java.util.*

@Service
class BuyBackgroundFacade(
    orchestratorFactory: OrchestratorFactory,
    identityApi: IdentityApi,
    renderApi: RenderApi,

    private val saleService: SaleService,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)
    private lateinit var backgroundBuyOrchestrator: Orchestrator<String, Unit>

    fun buyBackground(token: String, item: String) {
        backgroundBuyOrchestrator.sagaSync(
            item,
            mapOf(
                "token" to token,
                "idempotencyKey" to UUID.randomUUID().toString(),
                TRACE_ID to MDC.get(TRACE_ID),
                USER_ID to MDC.get(USER_ID),
            )
        ).decodeResultOrThrow(Unit::class)
    }

    init {
        backgroundBuyOrchestrator = orchestratorFactory.create<String>("buy background facade")
            .startWithContext(TraceIdContextOrchestrator { context, item ->
                val sale = saleService.getByTypeAndItem(SaleType.BACKGROUND, item)
                val user = identityApi.getUserByToken(context.decodeContext("token", String::class))

                require(sale.getCount() > 0) {
                    "Cannot buy item : \"${sale.type}\" cause its count : \"${sale.getCount()}\" == 0"
                }

                require(user.points.toLong() >= sale.price) {
                    "Cannot buy item: \"${sale.type}\" cause not enough points"
                }

                sale
            })
            .joinWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, sale ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)
                    identityApi.decreasePoint(token, idempotencyKey, sale.price.toString())

                    sale
                },
                contextRollback = TraceIdContextRollback { context, sale ->
                    val token = context.decodeContext("token", String::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    logger.warn("Cannot buy background rollback buyer point...")
                    identityApi.increasePoint(token, idempotencyKey, sale.price.toString())
                    logger.warn("Cannot buy background rollback buyer point success")
                }
            )
            .joinWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, sale ->
                    val token = context.decodeContext("token", String::class)

                    renderApi.addBackground(token, sale.item)
                    sale
                },
                contextRollback = TraceIdContextRollback { context, sale ->
                    val token = context.decodeContext("token", String::class)

                    logger.warn("Cannot buy background rollback item count...")
                    renderApi.deleteBackground(token, sale.item)
                    logger.warn("Cannot buy background rollback item count success")
                }
            )
            .commitWithContext(TraceIdContextOrchestrator { _, sale ->
                saleService.buyBySaleTypeAndItem(sale.type, sale.item)
            })
    }
}
