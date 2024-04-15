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
        val oauthUserResponse = oauth2Api.getOauthUsername(oauth2Api.getToken(code))

        val user = when (userService.existsUser(oauthUserResponse.username)) {
            true -> userService.getUserByName(oauthUserResponse.username)
            else -> {
                val contributedYears =
                    contributionApi.getAllContributionYearsWithToken(oauthUserResponse.username)
                val contributionCountPerYears =
                    contributionApi.getContributionCountWithToken(
                        oauthUserResponse.username,
                        contributedYears
                    )

                userService.newUser(
                    oauthUserResponse.username,
                    oauthUserResponse.profileImage,
                    contributionCountPerYears
                )
            }
        }

        return tokenManager.createToken(user).withType()
    }
}
