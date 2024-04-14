package org.gitanimals.identity.app

import org.gitanimals.identity.domain.User
import org.gitanimals.identity.domain.UserService
import org.springframework.stereotype.Service

@Service
class LoginFacade(
    private val oauth2Api: Oauth2Api,
    private val userService: UserService,
    private val contributionApi: ContributionApi,
) {

    fun login(code: String): User {
        val username = oauth2Api.getOauthUsername(oauth2Api.getToken(code))

        if (userService.existsUser(username)) {
            return userService.getUserByName(username)
        }

        val contributedYears = contributionApi.getAllContributionYearsWithToken(username)
        val contributionCountPerYears =
            contributionApi.getContributionCountWithToken(username, contributedYears)

        return userService.newUser(username, contributionCountPerYears)
    }
}
