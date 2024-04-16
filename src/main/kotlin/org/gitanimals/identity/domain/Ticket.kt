package org.gitanimals.identity.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Table(name = "ticket")
@Entity(name = "ticket")
class Ticket(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "subject", nullable = false)
    val subject: String,

    @Column(name = "isUsed", nullable = false)
    var isUsed: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    val ticketType: TicketType,

    @Version
    private val version: Long? = null,
) : AbstractTime() {

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    lateinit var user: User

    fun use() {
        require(!isUsed) { "Already used ticket." }
        isUsed = true
    }

    fun unuse() {
        isUsed = false
    }
}
