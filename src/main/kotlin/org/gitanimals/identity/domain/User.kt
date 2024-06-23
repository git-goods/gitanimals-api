package org.gitanimals.identity.domain

import jakarta.persistence.*
import org.gitanimals.identity.core.AggregateRoot
import org.gitanimals.identity.core.IdGenerator
import org.gitanimals.identity.core.instant
import org.gitanimals.identity.core.toZonedDateTime
import kotlin.math.max
import kotlin.math.min

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

    @OneToMany(
        mappedBy = "user",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private val pointHistories: MutableList<PointHistory>,

    @Column(name = "profile_image", nullable = false)
    val profileImage: String,

    @Version
    private val version: Long? = null,
) : AbstractTime() {

    fun getPoints(): Long = points

    fun givePoint(point: Long, reason: String) {
        val current = instant().toZonedDateTime()
        pointHistories.removeAll { it.createdAt.toZonedDateTime().isBefore(current) }

        val todayGivePoints = pointHistories.sumOf { it.points }
        val givePoint = min(point, max(0, PER_DAY_GIVE_POINT_THRESHOLD - todayGivePoints))

        this.points += givePoint
        pointHistories.add(PointHistory.from(this, givePoint, reason))
    }

    fun decreasePoint(point: Long) {
        this.points -= point
    }

    fun increasePoint(point: Long) {
        this.points += point
    }

    companion object {
        private val JOIN_POINT_THRESHOLD = 100_000L
        private val PER_DAY_GIVE_POINT_THRESHOLD = 20000L

        fun newUser(name: String, points: Long, profileImage: String): User {
            return User(
                id = IdGenerator.generate(),
                name = name,
                points = min(points, JOIN_POINT_THRESHOLD),
                pointHistories = mutableListOf(),
                profileImage = profileImage,
            )
        }
    }
}
