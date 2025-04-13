package org.gitanimals.gotcha.app

import org.gitanimals.core.TraceIdContextOrchestrator
import org.gitanimals.core.TraceIdContextRollback
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ID
import org.gitanimals.gotcha.app.response.GotchaResponseV3
import org.gitanimals.gotcha.domain.DropRateClient
import org.gitanimals.gotcha.domain.GotchaService
import org.gitanimals.gotcha.domain.GotchaType
import org.gitanimals.gotcha.domain.response.GotchaResponse
import org.rooftop.netx.api.*
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service

@Service
class GotchaFacadeV3(
    orchestratorFactory: OrchestratorFactory,
    private val gotchaService: GotchaService,
    private val userApi: UserApi,
    private val renderApi: RenderApi,
    private val dropRateClient: DropRateClient,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)
    private lateinit var gotchaOrchestrator: Orchestrator<String, List<GotchaResponseV3>>

    fun gotcha(token: String, gotchaType: GotchaType, count: Int): List<GotchaResponseV3> {
        require(count in 1..10) { "Gotcha count must between 1..10" }

        return gotchaOrchestrator
            .sagaSync(
                gotchaType.name, mapOf(
                    "token" to token,
                    "count" to count,
                    TRACE_ID to MDC.get(TRACE_ID),
                    USER_ID to MDC.get(USER_ID),
                )
            ).decodeResultOrThrow(object : TypeReference<List<GotchaResponseV3>>() {})
    }

    init {
        this.gotchaOrchestrator = orchestratorFactory.create<String>("gotchaOrchestratorV3")
            .startWithContext(contextOrchestrate = TraceIdContextOrchestrator { context, gotchaTypeName ->
                val token = context.decodeContext("token", String::class)
                val user = userApi.getUserByToken(token)
                val count = context.decodeContext("count", Int::class)
                context.set("user", user)

                val gotchaType = GotchaType.valueOf(gotchaTypeName)

                val gotchaResponses = mutableListOf<GotchaResponse>()
                repeat(count) {
                    gotchaResponses.add(gotchaService.gotcha(user.points.toLong(), gotchaType))
                }
                gotchaResponses.toList()
            })
            .joinWithContext(
                contextOrchestrate = GotchaResponsesOrchestrate { context, gotchaResponses ->
                    val token = context.decodeContext("token", String::class)

                    userApi.decreasePoint(
                        token,
                        gotchaResponses[0].idempotency,
                        gotchaResponses[0].point * gotchaResponses.size
                    )

                    gotchaResponses
                },
                contextRollback = GotChaResponsesRollback { context, gotchaResponses ->
                    val token = context.decodeContext("token", String::class)

                    logger.warn("Fail to gotcha increase point...")
                    userApi.increasePoint(
                        token,
                        gotchaResponses[0].idempotency,
                        gotchaResponses[0].point * gotchaResponses.size
                    )
                    logger.warn("Fail to gotcha increase point success")
                }
            )
            .commitWithContext(GotchaResponseV3Orchestrate { context, gotchaResponses ->
                val token = context.decodeContext("token", String::class)

                val personaIds =
                    renderApi.addPersonas(
                        token,
                        gotchaResponses.map {
                            RenderApi.AddPersonaRequest(
                                idempotencyKey = it.idempotency,
                                personaName = it.name,
                            )
                        },
                    ).map { it.id }

                for (i in gotchaResponses.indices) {
                    gotchaResponses[i].id = personaIds[i]
                }

                gotchaResponses.map {
                    GotchaResponseV3(
                        name = it.name,
                        dropRate = dropRateClient.getDropRate(it.name).toString()
                    )
                }
            })
    }

    private class GotchaResponsesOrchestrate(
        orchestrate: ContextOrchestrate<List<GotchaResponse>, List<GotchaResponse>>,
    ) : TraceIdContextOrchestrator<List<GotchaResponse>, List<GotchaResponse>>(orchestrate) {

        override fun reified(): TypeReference<List<GotchaResponse>> =
            object : TypeReference<List<GotchaResponse>>() {}
    }

    private class GotChaResponsesRollback(
        rollback: ContextRollback<List<GotchaResponse>, Unit>,
    ) : TraceIdContextRollback<List<GotchaResponse>, Unit>(rollback) {

        override fun reified(): TypeReference<List<GotchaResponse>> =
            object : TypeReference<List<GotchaResponse>>() {}
    }

    private class GotchaResponseV3Orchestrate(
        orchestrate: ContextOrchestrate<List<GotchaResponse>, List<GotchaResponseV3>>,
    ) : TraceIdContextOrchestrator<List<GotchaResponse>, List<GotchaResponseV3>>(orchestrate) {

        override fun reified(): TypeReference<List<GotchaResponse>> =
            object : TypeReference<List<GotchaResponse>>() {}
    }
}
