package org.gitanimals.coupon.app

import org.gitanimals.coupon.app.response.UserResponse


fun interface IdentityApi {

    fun getUserByToken(token: String): UserResponse
}
