package org.gitanimals.identity.domain

import jakarta.persistence.*
import org.gitanimals.auction.core.IdGenerator
import org.gitanimals.identity.core.instant
import org.hibernate.annotations.ColumnDefault
import java.time.Instant

@Entity
@Table(name = "point_history")
class PointHistory(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(name = "points", nullable = false)
    val points: Long,

    @Column(name = "reason", nullable = false)
    val reason: String,

    @Column(name = "created_at")
    val createdAt: Instant = instant(),
) {

    companion object {

        fun from(user: User, points: Long, reason: String) =
            PointHistory(
                id = IdGenerator.generate(),
                user = user,
                points = points,
                reason = reason,
            )
    }
}
