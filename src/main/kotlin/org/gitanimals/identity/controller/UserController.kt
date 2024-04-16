package org.gitanimals.identity.controller

import org.gitanimals.identity.app.Token
import org.gitanimals.identity.app.UserFacade
import org.gitanimals.identity.controller.request.TicketRequest
import org.gitanimals.identity.controller.response.UserResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userFacade: UserFacade,
) {

    @GetMapping("/users")
    fun getUserByToken(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
    ): UserResponse {
        val user = userFacade.getUserByToken(Token.from(token))
        return UserResponse.from(user)
    }

    @PutMapping("/users/tickets")
    fun useTickets(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestBody ticketRequest: TicketRequest,
    ) {
        val user = userFacade.getUserByToken(Token.from(token))
        userFacade.useTicket(user.id, ticketRequest.id, ticketRequest.behavior)
    }
}
