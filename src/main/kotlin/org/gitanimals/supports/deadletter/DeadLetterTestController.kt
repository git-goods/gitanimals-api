package org.gitanimals.supports.deadletter

import org.gitanimals.core.TraceIdContextOrchestrator
import org.gitanimals.core.TraceIdContextRollback
import org.rooftop.netx.api.OrchestratorFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class DeadLetterTestController(
    private val deadLetterEventPublisher: DeadLetterEventPublisher,
    private val orchestratorFactory: OrchestratorFactory,
) {

    @GetMapping("/test/dead-letter")
    fun test(
        @RequestParam("message") message: String,
    ) {
        val orchestrator = orchestratorFactory.create<String>("dead-letter-test")
            .startWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { _, it -> it },
                contextRollback = TraceIdContextRollback { _, it ->
                    it
                }
            )
            .joinWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { _, it -> it },
                contextRollback = TraceIdContextRollback { _, it ->
                    throw IllegalStateException("add dead letter")
                }
            )
            .commitWithContext(TraceIdContextOrchestrator { _, _ ->
                throw IllegalStateException("test dead-letter")
            })

        val result = orchestrator.sagaSync(message, context = mapOf("hello" to "world"))

        if (result.isSuccess.not()) {
            result.throwError()
        }
    }
}
