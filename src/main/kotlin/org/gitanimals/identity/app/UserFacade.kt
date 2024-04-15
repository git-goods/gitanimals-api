package org.gitanimals.identity.app

import org.gitanimals.identity.domain.User
import org.gitanimals.identity.domain.UserService

class UserFacade(
    private val userService: UserService,
    private val tokenManager: TokenManager,
) {

    fun getUserByToken(token: Token): User {
        val userId = tokenManager.getUserId(token)

        return userService.getUserById(userId)
    }
}
