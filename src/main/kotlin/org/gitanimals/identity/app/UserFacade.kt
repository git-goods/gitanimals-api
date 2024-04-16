package org.gitanimals.identity.app

import org.gitanimals.identity.app.event.TicketUsed
import org.gitanimals.identity.domain.User
import org.gitanimals.identity.domain.UserService
import org.rooftop.netx.api.SagaManager
import org.springframework.stereotype.Service

@Service
class UserFacade(
    private val userService: UserService,
    private val tokenManager: TokenManager,
    private val sagaManager: SagaManager,
) {

    fun getUserByToken(token: Token): User {
        val userId = tokenManager.getUserId(token)

        return userService.getUserById(userId)
    }

    fun useTicket(userId: Long, ticketId: Long, behavior: String) =
        sagaManager.startSync(TicketUsed(userId, ticketId, behavior))
}
