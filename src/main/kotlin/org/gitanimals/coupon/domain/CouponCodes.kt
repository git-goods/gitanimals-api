package org.gitanimals.coupon.domain

enum class CouponCodes {

    NEW_USER_BONUS_PET,
    ;

    companion object {
        fun getByCode(code: String): CouponCodes {
            return CouponCodes.valueOf(code.uppercase())
        }
    }
}
