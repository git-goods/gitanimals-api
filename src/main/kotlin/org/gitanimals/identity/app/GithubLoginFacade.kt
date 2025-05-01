package org.gitanimals.identity.app

import org.gitanimals.identity.domain.EntryPoint
import org.gitanimals.identity.domain.UserService
import org.springframework.stereotype.Service

@Service
class GithubLoginFacade(
    private val oauth2Api: Oauth2Api,
    private val userService: UserService,
    private val contributionApi: ContributionApi,
    private val tokenManager: TokenManager,
) {

    fun login(code: String): String {
        val oauthUserResponse = oauth2Api.getOauthUsername(oauth2Api.getToken(code))

        val user = when (userService.existsUser(oauthUserResponse.username, EntryPoint.GITHUB)) {
            true -> userService.getUserByNameAndEntryPoint(
                oauthUserResponse.username,
                EntryPoint.GITHUB,
            )

            else -> {
                val contributedYears =
                    contributionApi.getAllContributionYearsWithToken(oauthUserResponse.username)
                val contributionCountPerYears =
                    contributionApi.getContributionCountWithToken(
                        oauthUserResponse.username,
                        contributedYears
                    )

                userService.newUser(
                    username = oauthUserResponse.username,
                    entryPoint = EntryPoint.GITHUB,
                    profileImage = oauthUserResponse.profileImage,
                    contributionPerYears = contributionCountPerYears,
                )
            }
        }

        return tokenManager.createToken(user).withType()
    }
}
