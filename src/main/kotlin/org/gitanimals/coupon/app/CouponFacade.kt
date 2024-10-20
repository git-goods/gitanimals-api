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
            CouponCodes.HALLOWEEN_2024,
            CouponCodes.HALLOWEEN_2024_STAR_BONUS -> {
                val picked = halloweenCandidates.random()
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
        private val halloweenCandidates = mutableListOf<String>().also { candidates ->
            repeat(500) {
                candidates.add("SLIME_PUMPKIN_1")
            }
            repeat(302) {
                candidates.add("SLIME_PUMPKIN_2")
            }
            repeat(150) {
                candidates.add("GHOST")
            }
            repeat(30) {
                candidates.add("GHOST_KING")
            }
            repeat(15) {
                candidates.add("SCREAM")
            }
            repeat(3) {
                candidates.add("SCREAM_GHOST")
            }
        }
    }
}
