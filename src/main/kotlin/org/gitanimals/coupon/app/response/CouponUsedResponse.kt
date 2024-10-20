package org.gitanimals.coupon.app.response

data class CouponUsedResponse(
    val result: String
) {

    fun toResponse() = mapOf("result" to result)
}
