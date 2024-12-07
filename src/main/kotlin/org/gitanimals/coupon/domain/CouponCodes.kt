package org.gitanimals.coupon.domain

enum class CouponCodes {

    NEW_USER_BONUS_PET,
    CHRISTMAS_2024,
    CHRISTMAS_2024_STAR_BONUS,
    ;

    companion object {
        fun getByCode(code: String): CouponCodes {
            return CouponCodes.valueOf(code.uppercase())
        }
    }
}
