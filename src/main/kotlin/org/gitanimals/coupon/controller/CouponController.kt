package org.gitanimals.coupon.controller

import org.gitanimals.coupon.app.CouponFacade
import org.gitanimals.coupon.controller.request.CouponRequest
import org.gitanimals.coupon.controller.response.CouponResponses
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

    @GetMapping("/coupons/users")
    @ResponseStatus(HttpStatus.OK)
    fun getUsedCoupons(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
    ): CouponResponses = CouponResponses.from(couponFacade.getUsedCoupons(token))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(illegalArgumentException: IllegalArgumentException): ErrorResponse {
        return ErrorResponse.from(illegalArgumentException)
    }

}
