package org.gitanimals.coupon.controller.response

import org.gitanimals.coupon.domain.Coupon

data class CouponResponses(
    val coupons: List<CouponResponse>
) {

    companion object {
        fun from(coupons: List<Coupon>): CouponResponses =
            CouponResponses(coupons.map { CouponResponse.from(it) })
    }
}
