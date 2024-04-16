package org.gitanimals.identity.saga

import org.gitanimals.identity.app.event.TicketUsed
import org.gitanimals.identity.domain.UserService
import org.rooftop.netx.api.*
import org.rooftop.netx.meta.SagaHandler

@SagaHandler
class UseTicketHandler(
    private val userService: UserService,
) {

    @SagaStartListener(event = TicketUsed::class, successWith = SuccessWith.PUBLISH_COMMIT)
    fun useTicket(sagaStartEvent: SagaStartEvent) {
        val ticketUsed = sagaStartEvent.decodeEvent(TicketUsed::class)

        val userId = ticketUsed.userId
        val ticketId = ticketUsed.id

        userService.useTicket(userId, ticketId)

        sagaStartEvent.setNextEvent(ticketUsed)
    }

    @SagaRollbackListener(event = TicketUsed::class)
    fun rollbackTicket(sagaRollbackEvent: SagaRollbackEvent) {
        val ticketUsed = sagaRollbackEvent.decodeEvent(TicketUsed::class)
        userService.rollbackTicket(ticketUsed.userId, ticketUsed.id)
    }
}
