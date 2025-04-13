package org.gitanimals.coupon.app

import org.gitanimals.coupon.app.response.UserResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.GetExchange


fun interface IdentityApi {

    @GetExchange("/users")
    fun getUserByToken(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String): UserResponse
}
