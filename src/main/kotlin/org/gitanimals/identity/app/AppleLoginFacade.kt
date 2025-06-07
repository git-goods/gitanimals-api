package org.gitanimals.identity.app

import org.gitanimals.identity.domain.EntryPoint
import org.gitanimals.identity.domain.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppleLoginFacade(
    @Value("\${login.secret}") private val loginSecret: String,
    private val tokenManager: TokenManager,
    private val userService: UserService,
) {

    fun login(loginSecret: String, username: String, profileImage: String): String {
        require(loginSecret == this.loginSecret) {
            "Fail to login cause wrong loginSecret"
        }

        val isExistsUser = userService.existsUser(username, EntryPoint.APPLE)

        val user = when (isExistsUser) {
            true -> userService.getUserByNameAndEntryPoint(username, EntryPoint.APPLE)
            false -> {
                userService.newUser(
                    username = username,
                    entryPoint = EntryPoint.APPLE,
                    profileImage = profileImage,
                    contributionPerYears = mapOf(),
                    authenticationId = username,
                )
            }
        }

        return tokenManager.createToken(user).withType()
    }
}
