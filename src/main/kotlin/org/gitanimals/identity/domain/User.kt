package org.gitanimals.identity.domain

import jakarta.persistence.*
import org.gitanimals.identity.core.AggregateRoot
import org.gitanimals.identity.core.IdGenerator
import org.gitanimals.identity.core.instant
import org.gitanimals.identity.core.toZonedDateTime
import org.slf4j.LoggerFactory
import kotlin.math.max
import kotlin.math.min

@AggregateRoot
@Table(
    name = "users", indexes = [
        Index(columnList = "username", unique = true),
        Index(columnList = "entry_point, authentication_id", unique = true),
    ]
)
@Entity(name = "users")
class User(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "username", nullable = false)
    private var name: String,

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

    @Embedded
    private val authInfo: UserAuthInfo,

    @Version
    private val version: Long? = null,
) : AbstractTime() {

    fun getName(): String = name

    fun getPoints(): Long = points

    fun getEntryPoint(): EntryPoint = authInfo.entryPoint

    fun findAuthenticationId(): String? = authInfo.authenticationId

    fun setAuthenticationId(authenticationId: String) {
        if (authInfo.authenticationId != null) {
            check(authInfo.authenticationId == authenticationId) {
                val message =
                    "Different authenticationId input. saved authenticationId: \"${authInfo.authenticationId}\", input authenticationId: \"$authenticationId\""
                logger.error(message)
                message
            }
        }
        authInfo.authenticationId = authenticationId
    }

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

        require(points >= 0) {
            "cannot decrease points cause point is \"${this.points}\""
        }
    }

    fun increasePoint(point: Long) {
        if (point < 0) {
            return
        }

        this.points += point
    }

    fun updateUsername(username: String) {
        this.name = username
    }

    companion object {
        private val logger = LoggerFactory.getLogger(User::class.simpleName)

        private const val JOIN_POINT_THRESHOLD = 100_000L
        private const val PER_DAY_GIVE_POINT_THRESHOLD = 20000L

        fun newUser(
            name: String,
            points: Long,
            profileImage: String,
            entryPoint: EntryPoint,
            authenticationId: String,
        ): User {
            return User(
                id = IdGenerator.generate(),
                name = name,
                points = min(points, JOIN_POINT_THRESHOLD),
                pointHistories = mutableListOf(),
                profileImage = profileImage,
                authInfo = UserAuthInfo(entryPoint, authenticationId),
            )
        }
    }
}
