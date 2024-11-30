package org.gitanimals.inbox.controller

import org.gitanimals.inbox.infra.event.InboxInputEvent
import org.rooftop.netx.api.SagaManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class InboxTestController(
    private val sagaManager: SagaManager,
    @Value("\${test.secret}") private val testSecret: String,
) {

    @PostMapping("test/input-inbox")
    @ResponseStatus(HttpStatus.OK)
    fun inputInbox(
        @RequestHeader("Test-Secret") testSecret: String,
        @RequestBody inboxInputEvent: InboxInputEvent,
    ) {
        require(testSecret == this.testSecret) { "Invalid testSecret testSecret" }
        sagaManager.startSync(inboxInputEvent)
    }
}
