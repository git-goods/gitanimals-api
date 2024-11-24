package org.gitanimals.gotcha.app

import org.gitanimals.gotcha.app.GotchaFacadeV3.*
import org.gitanimals.gotcha.app.response.GotchaResponseV3
import org.gitanimals.gotcha.domain.DropRateClient
import org.gitanimals.gotcha.domain.GotchaService
import org.gitanimals.gotcha.domain.GotchaType
import org.gitanimals.gotcha.domain.response.GotchaResponse
import org.rooftop.netx.api.*
import org.springframework.stereotype.Service

@Service
class GotchaFacadeV3(
    orchestratorFactory: OrchestratorFactory,
    private val gotchaService: GotchaService,
    private val userApi: UserApi,
    private val renderApi: RenderApi,
    private val dropRateClient: DropRateClient,
) {

    private lateinit var gotchaOrchestrator: Orchestrator<String, List<GotchaResponseV3>>

    fun gotcha(token: String, gotchaType: GotchaType, count: Int): List<GotchaResponseV3> {
        require(count in 1..10) { "Gotcha count must between 1..10" }

        return gotchaOrchestrator
            .sagaSync(gotchaType.name, mapOf("token" to token, "count" to count))
            .decodeResultOrThrow(object : TypeReference<List<GotchaResponseV3>>() {})
    }

    init {
        this.gotchaOrchestrator = orchestratorFactory.create<String>("gotchaOrchestratorV3")
            .startWithContext(contextOrchestrate = { context, gotchaTypeName ->
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

                    userApi.increasePoint(
                        token,
                        gotchaResponses[0].idempotency,
                        gotchaResponses[0].point * gotchaResponses.size
                    )
                }
            )
            .commitWithContext(GotchaResponseV3Orchestrate { context, gotchaResponses ->
                val token = context.decodeContext("token", String::class)

                val personaIds =
                    renderApi.addPersonas(
                        token,
                        gotchaResponses.map { it.idempotency },
                        gotchaResponses.map { it.name },
                    )

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

    private fun interface GotchaResponsesOrchestrate :
        ContextOrchestrate<List<GotchaResponse>, List<GotchaResponse>> {

        override fun reified(): TypeReference<List<GotchaResponse>> =
            object : TypeReference<List<GotchaResponse>>() {}
    }

    private fun interface GotChaResponsesRollback
        : ContextRollback<List<GotchaResponse>, Unit> {

        override fun reified(): TypeReference<List<GotchaResponse>> =
            object : TypeReference<List<GotchaResponse>>() {}
    }

    private fun interface GotchaResponseV3Orchestrate :
        ContextOrchestrate<List<GotchaResponse>, List<GotchaResponseV3>> {

        override fun reified(): TypeReference<List<GotchaResponse>> =
            object : TypeReference<List<GotchaResponse>>() {}
    }
}
