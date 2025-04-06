package org.gitanimals.supports.deadletter

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
                contextOrchestrate = { _, it -> it },
                contextRollback = { _, it ->
                    it
                }
            )
            .joinWithContext(
                contextOrchestrate = { _, it -> it },
                contextRollback = { _, it ->
                    throw IllegalStateException("add dead letter")
                }
            )
            .commitWithContext { _, _ ->
                throw IllegalStateException("test dead-letter")
            }

        orchestrator.sagaSync(message, context = mapOf("hello" to "world"))
    }
}
