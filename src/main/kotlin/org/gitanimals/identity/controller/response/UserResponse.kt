package org.gitanimals.identity.controller.response

import org.gitanimals.identity.domain.Ticket
import org.gitanimals.identity.domain.User

data class UserResponse(
    val id: String,
    val username: String,
    val points: String,
    val profileImage: String,
    val ticketResponse: List<TicketResponse>,
) {

    data class TicketResponse(
        val id: Long,
        val subject: String,
        val isUsed: Boolean,
    ) {

        companion object {
            fun from(ticket: Ticket): TicketResponse =
                TicketResponse(ticket.id, ticket.subject, ticket.isUsed)
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
