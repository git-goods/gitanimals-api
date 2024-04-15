package org.gitanimals.identity.app

import org.gitanimals.identity.domain.UserService
import org.springframework.stereotype.Service

@Service
class LoginFacade(
    private val oauth2Api: Oauth2Api,
    private val userService: UserService,
    private val contributionApi: ContributionApi,
    private val tokenManager: TokenManager,
) {

    fun login(code: String): String {
        val username = oauth2Api.getOauthUsername(oauth2Api.getToken(code))

        val user = when (userService.existsUser(username)) {
            true -> userService.getUserByName(username)
            else -> {
                val contributedYears = contributionApi.getAllContributionYearsWithToken(username)
                val contributionCountPerYears =
                    contributionApi.getContributionCountWithToken(username, contributedYears)

                userService.newUser(username, contributionCountPerYears)
            }
        }

        return tokenManager.createToken(user).withType()
    }
}
