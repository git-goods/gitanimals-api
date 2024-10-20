package org.gitanimals.coupon.controller.request

data class CouponRequest(
    val code: String,
    val dynamic: String = "",
)
