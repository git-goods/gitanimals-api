package org.gitanimals.coupon.domain

import jakarta.persistence.*
import org.gitanimals.coupon.core.IdGenerator
import org.gitanimals.identity.core.AggregateRoot
import java.time.Instant

@AggregateRoot
@Entity(name = "coupon")
@Table(name = "coupon", indexes = [Index(columnList = "code")])
class Coupon(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "code", columnDefinition = "VARCHAR(100)", length = 100, nullable = false)
    val code: String,

    @Column(name = "used_at", columnDefinition = "TIMESTAMP(6)", nullable = false)
    val usedAt: Instant,
) {

    init {
        require(code.length <= 100) { "Coupon code length must be under \"100\"" }
    }

    companion object {

        fun newCoupon(userId: Long, code: String): Coupon {
            return Coupon(
                id = IdGenerator.generate(),
                userId = userId,
                code = code,
                usedAt = Instant.now(),
            )
        }
    }
}
