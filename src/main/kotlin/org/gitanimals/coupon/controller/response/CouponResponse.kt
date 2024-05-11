package org.gitanimals.coupon.controller.response

import com.fasterxml.jackson.annotation.JsonFormat
import org.gitanimals.coupon.domain.Coupon
import java.time.LocalDateTime
import java.time.ZoneOffset

data class CouponResponse(
    val id: String,
    val userId: String,
    val code: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddTHH:mm:ssZ", timezone = "UTC")
    val usedAt: LocalDateTime,
) {

    companion object {
        fun from(coupon: Coupon): CouponResponse =
            CouponResponse(
                id = coupon.id.toString(),
                userId = coupon.userId.toString(),
                code = coupon.code,
                usedAt = LocalDateTime.ofInstant(coupon.usedAt, ZoneOffset.UTC),
            )
    }
}
