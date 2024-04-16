package org.gitanimals.identity.domain

import jakarta.persistence.*
import org.gitanimals.identity.core.AggregateRoot
import org.gitanimals.identity.core.IdGenerator
import org.hibernate.annotations.BatchSize

@AggregateRoot
@Table(
    name = "users", indexes = [
        Index(columnList = "username", unique = true)
    ]
)
@Entity(name = "users")
class User(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "username", nullable = false)
    val name: String,

    @Column(name = "points", nullable = false)
    private var points: Long,

    @Column(name = "profile_image", nullable = false)
    val profileImage: String,

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val tickets: MutableList<Ticket>,

    @Version
    private val version: Long? = null,
) : AbstractTime() {

    init {
        tickets.forEach {
            it.user = this
        }
    }

    fun getPoints(): Long = points

    fun givePoint(point: Long) {
        this.points += point
    }

    companion object {

        fun newUser(name: String, points: Long, profileImage: String): User {
            val ticketForNewUser = TicketType.NEW_USER_BONUS_PET.toTicket()

            return User(
                id = IdGenerator.generate(),
                name = name,
                points = points,
                profileImage = profileImage,
                tickets = mutableListOf(ticketForNewUser),
            )
        }
    }
}
