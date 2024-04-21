package org.gitanimals.coupon.app.event

data class CouponUsed(
    val userId: Long,
    val code: String
)
