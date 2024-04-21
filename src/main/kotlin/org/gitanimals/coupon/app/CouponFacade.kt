package org.gitanimals.coupon.app

import org.gitanimals.coupon.app.event.CouponUsed
import org.gitanimals.coupon.domain.CouponService
import org.rooftop.netx.api.SagaManager
import org.springframework.stereotype.Service

@Service
class CouponFacade(
    private val couponService: CouponService,
    private val sagaManager: SagaManager,
    private val identityApi: IdentityApi,
) {

    fun useCoupon(token: String, code: String) {
        val user = identityApi.getUserByToken(token)

        require(couponService.isValidCoupon(user.id.toLong(), code)) {
            "Cannot use coupon code $code"
        }

        sagaManager.startSync(CouponUsed(user.id.toLong(), code))
    }
}