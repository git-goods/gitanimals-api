package org.gitanimals.coupon.app

import org.gitanimals.coupon.app.event.CouponUsed
import org.gitanimals.coupon.app.response.CouponUsedResponse
import org.gitanimals.coupon.domain.Coupon
import org.gitanimals.coupon.domain.CouponCodes
import org.gitanimals.coupon.domain.CouponService
import org.rooftop.netx.api.SagaManager
import org.springframework.stereotype.Service

@Service
class CouponFacade(
    private val couponService: CouponService,
    private val sagaManager: SagaManager,
    private val identityApi: IdentityApi,
) {

    fun useCoupon(token: String, code: String, dynamic: String): CouponUsedResponse? {
        val user = identityApi.getUserByToken(token)

        require(couponService.isValidCoupon(user.id.toLong(), code)) {
            "Cannot use coupon code $code"
        }

        return when (CouponCodes.getByCode(code)) {
            CouponCodes.NEW_USER_BONUS_PET -> useBonusCoupon(token, code, dynamic)
            CouponCodes.CHRISTMAS_2024_STAR_BONUS,
            CouponCodes.CHRISTMAS_2024 -> {
                val picked = christmasCandidates.random()
                useBonusCoupon(token, code, picked)
                return CouponUsedResponse(picked)
            }
        }
    }

    private fun useBonusCoupon(token: String, code: String, dynamic: String): CouponUsedResponse? {
        val user = identityApi.getUserByToken(token)

        sagaManager.startSync(CouponUsed(user.id.toLong(), user.username, code, dynamic))
        return null
    }

    fun getUsedCoupons(token: String): List<Coupon> {
        val user = identityApi.getUserByToken(token)

        return couponService.getCouponsByUserId(user.id.toLong())
    }

    private companion object {
        private val christmasCandidates = mutableListOf<String>().also { candidates ->
            repeat(500) {
                candidates.add("SNOWMAN")
            }
            repeat(302) {
                candidates.add("HAMSTER_SANTA")
            }
            repeat(150) {
                candidates.add("LITTLE_CHICK_SANTA")
            }
            repeat(30) {
                candidates.add("RABBIT_BROWN_RUDOLPH")
            }
            repeat(15) {
                candidates.add("DESSERT_FOX_RUDOLPH")
            }
            repeat(3) {
                candidates.add("SNOWMAN_MELT")
            }
        }
    }
}
