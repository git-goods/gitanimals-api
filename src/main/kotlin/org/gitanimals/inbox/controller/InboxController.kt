package org.gitanimals.inbox.controller

import org.gitanimals.inbox.app.InboxFacade
import org.gitanimals.inbox.controller.response.InboxResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class InboxController(
    private val inboxFacade: InboxFacade,
) {

    @GetMapping("/inboxes")
    fun getAllInboxes(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
    ): InboxResponse {
        val inboxApplication = inboxFacade.findAllUnreadByToken(token)

        return InboxResponse.from(inboxApplication)
    }
}
