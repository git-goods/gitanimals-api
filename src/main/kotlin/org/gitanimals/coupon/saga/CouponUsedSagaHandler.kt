package org.gitanimals.coupon.saga

import org.gitanimals.coupon.app.event.CouponUsed
import org.gitanimals.coupon.domain.CouponService
import org.rooftop.netx.api.SagaRollbackEvent
import org.rooftop.netx.api.SagaRollbackListener
import org.rooftop.netx.api.SagaStartEvent
import org.rooftop.netx.api.SagaStartListener
import org.rooftop.netx.meta.SagaHandler

@SagaHandler
class CouponUsedSagaHandler(
    private val couponService: CouponService,
) {

    @SagaStartListener(event = CouponUsed::class)
    fun handleCouponUsedStartSaga(sagaStartEvent: SagaStartEvent) {
        val couponUsed = sagaStartEvent.decodeEvent(CouponUsed::class)

        couponService.useCoupon(couponUsed.userId, couponUsed.code)

        sagaStartEvent.setNextEvent(couponUsed)
    }

    @SagaRollbackListener(event = CouponUsed::class)
    fun handleCouponUsedRollbackSaga(sagaRollbackEvent: SagaRollbackEvent) {
        val couponUsed = sagaRollbackEvent.decodeEvent(CouponUsed::class)

        couponService.deleteCoupon(couponUsed.userId, couponUsed.code)
    }
}
