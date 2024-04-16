package org.gitanimals.identity.app.event

data class TicketUsed(
    val userId: Long,
    val id: Long,
    val behavior: String,
)
