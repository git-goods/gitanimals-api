package org.gitanimals.coupon.domain

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CouponService(
    private val couponRepository: CouponRepository,
    private val idempotencyRepository: IdempotencyRepository,
) {

    @Transactional
    fun useCoupon(userId: Long, code: String) {
        require(
            CouponCodes.entries.find { it.name == code.uppercase() } != null
        ) { "Invalid coupon code \"$code\"" }
        val idempotencyId = "$userId:$code"
        require(idempotencyRepository.findByIdOrNull(idempotencyId) == null) {
            "Duplicated coupon use request"
        }

        val coupon = Coupon.newCoupon(userId, code)
        val idempotency = Idempotency(idempotencyId)

        couponRepository.save(coupon)
        idempotencyRepository.save(idempotency)
    }

    @Transactional
    fun deleteCoupon(userId: Long, code: String) {
        val idempotencyId = "$userId:$code"

        couponRepository.deleteByUserIdAndCode(userId, code)
        idempotencyRepository.deleteById(idempotencyId)
    }

    fun isValidCoupon(userId: Long, code: String): Boolean {
        val isExistsCoupon = CouponCodes.entries.find { it.name == code.uppercase() } != null
        if (!isExistsCoupon) {
            return false
        }
        return !couponRepository.existsByUserIdAndCode(userId, code)
    }

    fun getCouponsByUserId(userId: Long): List<Coupon> {
        return couponRepository.findAllByUserId(userId)
    }
}
