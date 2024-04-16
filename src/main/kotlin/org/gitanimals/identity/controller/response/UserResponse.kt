package org.gitanimals.identity.controller.response

import org.gitanimals.identity.domain.Ticket
import org.gitanimals.identity.domain.TicketType
import org.gitanimals.identity.domain.User

data class UserResponse(
    val id: String,
    val username: String,
    val points: String,
    val profileImage: String,
    val tickets: List<TicketResponse>,
) {

    data class TicketResponse(
        val id: String,
        val subject: String,
        val isUsed: Boolean,
        val type: TicketType,
    ) {

        companion object {
            fun from(ticket: Ticket): TicketResponse =
                TicketResponse(ticket.id.toString(), ticket.subject, ticket.isUsed, ticket.ticketType)
        }
    }

    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                user.id.toString(),
                user.name,
                user.getPoints().toString(),
                user.profileImage,
                user.tickets.map { TicketResponse.from(it) }.toList()
            )
        }
    }
}
