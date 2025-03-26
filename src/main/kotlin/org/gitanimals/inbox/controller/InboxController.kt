package org.gitanimals.inbox.controller

import org.gitanimals.inbox.app.InboxFacade
import org.gitanimals.inbox.controller.request.InboxInputRequest
import org.gitanimals.inbox.controller.response.ErrorResponse
import org.gitanimals.inbox.controller.response.InboxResponse
import org.gitanimals.inbox.domain.InboxService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class InboxController(
    private val inboxFacade: InboxFacade,
    private val inboxService: InboxService,
) {

    @GetMapping("/inboxes")
    @ResponseStatus(HttpStatus.OK)
    fun getAllInboxes(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
    ): InboxResponse {
        val inboxApplication = inboxFacade.findAllUnreadByToken(token)

        return InboxResponse.from(inboxApplication)
    }

    @DeleteMapping("/inboxes/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun readInboxByTokenAndId(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("id") id: Long,
    ) = inboxFacade.readInboxByTokenAndId(token, id)

    @PostMapping("/internals/inboxes")
    @ResponseStatus(HttpStatus.OK)
    fun inputInboxToSpecificUser(
        @RequestParam("userId") userId: Long,
        @RequestBody inboxInputRequest: InboxInputRequest,
    ) = inboxService.inputInbox(
        userId = inboxInputRequest.inboxData.userId,
        type = inboxInputRequest.inboxData.type,
        title = inboxInputRequest.inboxData.title,
        body = inboxInputRequest.inboxData.body,
        image = inboxInputRequest.inboxData.image,
        redirectTo = inboxInputRequest.inboxData.redirectTo,
        publisher = inboxInputRequest.publisher.publisher,
        publishedAt = inboxInputRequest.publisher.publishedAt,
    )

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(illegalArgumentException: IllegalArgumentException): ErrorResponse {
        return ErrorResponse.from(illegalArgumentException)
    }
}
