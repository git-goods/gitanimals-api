package org.gitanimals.gotcha.app

import org.gitanimals.gotcha.domain.GotchaService
import org.gitanimals.gotcha.domain.GotchaType
import org.gitanimals.gotcha.domain.response.GotchaResponse
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.springframework.stereotype.Service

@Service
class GotchaFacade(
    orchestratorFactory: OrchestratorFactory,
    private val gotchaService: GotchaService,
    private val userApi: UserApi,
    private val renderApi: RenderApi,
) {

    private lateinit var gotchaOrchestrator: Orchestrator<String, GotchaResponse>

    fun gotcha(token: String, gotchaType: GotchaType, count: Int): List<GotchaResponse> {
        require(count in 1..10) { "Gotcha count must between 1..10" }

        val gotchaResponses = mutableListOf<GotchaResponse>()

        repeat(count) {
            gotchaResponses.add(
                gotchaOrchestrator
                    .sagaSync(gotchaType.name, mapOf("token" to token))
                    .decodeResultOrThrow(GotchaResponse::class)
            )
        }

        return gotchaResponses
    }

    init {
        this.gotchaOrchestrator = orchestratorFactory.create<String>("gotchaOrchestrator")
            .startWithContext(contextOrchestrate = { context, gotchaTypeName ->
                val token = context.decodeContext("token", String::class)
                val user = userApi.getUserByToken(token)
                context.set("user", user)

                val gotchaType = GotchaType.valueOf(gotchaTypeName)

                gotchaService.gotcha(user.points.toLong(), gotchaType)
            })
            .joinWithContext(
                contextOrchestrate = { context, gotchaResponse ->
                    val token = context.decodeContext("token", String::class)

                    userApi.decreasePoint(token, gotchaResponse.idempotency, gotchaResponse.point)

                    gotchaResponse
                },
                contextRollback = { context, gotchaResponse ->
                    val token = context.decodeContext("token", String::class)

                    userApi.increasePoint(token, gotchaResponse.idempotency, gotchaResponse.point)
                }
            )
            .commitWithContext { context, gotchaResponse ->
                val token = context.decodeContext("token", String::class)

                val personaId =
                    renderApi.addPersona(token, gotchaResponse.idempotency, gotchaResponse.name)

                gotchaResponse.id = personaId
                gotchaResponse
            }
    }
}
