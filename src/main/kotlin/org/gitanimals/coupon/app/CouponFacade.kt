package org.gitanimals.coupon.app

import org.gitanimals.core.auth.InternalAuth
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
    private val internalAuth: InternalAuth,
) {

    fun useCoupon(token: String, code: String, dynamic: String): CouponUsedResponse? {
        val userId = internalAuth.getUserId()

        require(couponService.isValidCoupon(userId, code)) {
            "Cannot use coupon code $code"
        }

        return when (CouponCodes.getByCode(code)) {
            CouponCodes.NEW_USER_BONUS_PET -> useBonusCoupon(token, code, dynamic)
        }
    }

    private fun useBonusCoupon(token: String, code: String, dynamic: String): CouponUsedResponse? {
        val user = identityApi.getUserByToken(token)

        sagaManager.startSync(CouponUsed(user.id.toLong(), user.username, code, dynamic))
        return null
    }

    fun getUsedCoupons(): List<Coupon> {
        val userId = internalAuth.getUserId()

        return couponService.getCouponsByUserId(userId)
    }
}
