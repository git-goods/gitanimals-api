package org.gitanimals.coupon.controller

import org.gitanimals.coupon.app.CouponFacade
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class CouponController(
    private val couponFacade: CouponFacade
) {

    @GetMapping("/coupons")
    @ResponseStatus(HttpStatus.OK)
    fun useCoupon(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam(name = "code", required = true) code: String
    ) = couponFacade.useCoupon(token, code)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(illegalArgumentException: IllegalArgumentException): ErrorResponse {
        return ErrorResponse.from(illegalArgumentException)
    }

}
