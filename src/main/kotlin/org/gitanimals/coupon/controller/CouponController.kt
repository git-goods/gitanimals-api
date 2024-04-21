package org.gitanimals.coupon.controller

import org.gitanimals.coupon.app.CouponFacade
import org.gitanimals.coupon.controller.request.CouponRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class CouponController(
    private val couponFacade: CouponFacade
) {

    @PostMapping("/coupons")
    @ResponseStatus(HttpStatus.OK)
    fun useCoupon(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestBody couponRequest: CouponRequest,
    ) = couponFacade.useCoupon(token, couponRequest.code, couponRequest.dynamic)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(illegalArgumentException: IllegalArgumentException): ErrorResponse {
        return ErrorResponse.from(illegalArgumentException)
    }

}
