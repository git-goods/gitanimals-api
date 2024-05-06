package org.gitanimals.coupon.controller.response

import org.gitanimals.coupon.domain.Coupon
import java.time.Instant

data class CouponResponse(
    val id: String,
    val userId: String,
    val code: String,
    val usedAt: Instant,
) {

    companion object {
        fun from(coupon: Coupon): CouponResponse =
            CouponResponse(
                id = coupon.id.toString(),
                userId = coupon.userId.toString(),
                code = coupon.code,
                usedAt = coupon.usedAt,
            )
    }
}
