package org.gitanimals.identity.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Ticket(
    @Column(name = "subject", nullable = false)
    val subject: String,
    @Column(name = "isUsed", nullable = false)
    val isUsed: Boolean,
) : AbstractTime() {

    companion object {

        fun from(subject: String): Ticket = Ticket(subject, false)
    }
}
