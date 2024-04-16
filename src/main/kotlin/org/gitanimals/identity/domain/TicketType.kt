package org.gitanimals.identity.domain

import org.gitanimals.identity.core.IdGenerator

enum class TicketType {

    NEW_USER_BONUS_PET {
        override fun toTicket(): Ticket =
            Ticket(IdGenerator.generate(), "New user bonus pet", false, this)
    },
    ;

    abstract fun toTicket(): Ticket
}
