package org.gitanimals.identity.domain

import jakarta.persistence.*
import org.gitanimals.identity.core.AggregateRoot

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

    @ElementCollection
    @CollectionTable(name = "users_tickets")
    val tickets: MutableList<Ticket>,

    @Version
    private val version: Long? = null,
) : AbstractTime() {

    fun getPoints(): Long = points

    fun givePoint(point: Long) {
        this.points += point
    }

    companion object {

        fun newUser(id: Long, name: String, points: Long, profileImage: String): User {
            return User(
                id = id,
                name = name,
                points = points,
                profileImage = profileImage,
                tickets = mutableListOf(Ticket.from("Bonus pet")),
            )
        }
    }
}
