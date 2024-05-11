package org.gitanimals.coupon.app

import org.gitanimals.coupon.app.event.CouponUsed
import org.gitanimals.coupon.controller.response.CouponResponses
import org.gitanimals.coupon.domain.Coupon
import org.gitanimals.coupon.domain.CouponService
import org.rooftop.netx.api.SagaManager
import org.springframework.stereotype.Service

@Service
class CouponFacade(
    private val couponService: CouponService,
    private val sagaManager: SagaManager,
    private val identityApi: IdentityApi,
) {

    fun useCoupon(token: String, code: String, dynamic: String) {
        val user = identityApi.getUserByToken(token)

        require(couponService.isValidCoupon(user.id.toLong(), code)) {
            "Cannot use coupon code $code"
        }

        sagaManager.startSync(CouponUsed(user.id.toLong(), user.username, code, dynamic))
    }

    fun getUsedCoupons(token: String): List<Coupon> {
        val user = identityApi.getUserByToken(token)

        return couponService.getCouponsByUserId(user.id.toLong())
    }
}
