package org.gitanimals.identity.app

import org.gitanimals.core.event.EventLogger
import org.gitanimals.identity.domain.EntryPoint
import org.gitanimals.identity.domain.UserService
import org.springframework.stereotype.Service

@Service
class GithubLoginFacade(
    private val githubOauth2Api: GithubOauth2Api,
    private val userService: UserService,
    private val contributionApi: ContributionApi,
    private val tokenManager: TokenManager,
    private val eventLogger: EventLogger,
) {

    fun login(code: String): String {
        val oauthUserResponse = githubOauth2Api.getOauthUsername(githubOauth2Api.getToken(code))

        var isNewUser = false
        val user = when (userService.existsByEntryPointAndAuthenticationId(
            entryPoint = EntryPoint.GITHUB,
            authenticationId = oauthUserResponse.id,
        )) {
            true -> {
                runCatching {
                    userService.getUserByNameAndEntryPoint(
                        oauthUserResponse.username,
                        EntryPoint.GITHUB,
                    )
                }.getOrElse {
                    if (it is IllegalArgumentException) {
                        userService.updateUsernameByEntryPointAndAuthenticationId(
                            username = oauthUserResponse.username,
                            entryPoint = EntryPoint.GITHUB,
                            authenticationId = oauthUserResponse.id,
                        )
                        return@getOrElse userService.getUserByNameAndEntryPoint(
                            oauthUserResponse.username,
                            EntryPoint.GITHUB,
                        )
                    }
                    throw it
                }
            }

            false -> {
                if (userService.existsUser(
                        username = oauthUserResponse.username,
                        entryPoint = EntryPoint.GITHUB,
                    )
                ) {
                    userService.updateUserAuthInfoByUsername(
                        username = oauthUserResponse.username,
                        entryPoint = EntryPoint.GITHUB,
                        authenticationId = oauthUserResponse.id,
                    )

                    userService.getUserByNameAndEntryPoint(
                        username = oauthUserResponse.username,
                        entryPoint = EntryPoint.GITHUB,
                    )
                } else {
                    isNewUser = true
                    val contributedYears =
                        contributionApi.getAllContributionYearsWithToken(oauthUserResponse.username)
                    val contributionCountPerYears =
                        contributionApi.getContributionCountWithToken(
                            oauthUserResponse.username,
                            contributedYears,
                        )

                    userService.newUser(
                        username = oauthUserResponse.username,
                        entryPoint = EntryPoint.GITHUB,
                        authenticationId = oauthUserResponse.id,
                        profileImage = oauthUserResponse.profileImage,
                        contributionPerYears = contributionCountPerYears,
                    )
                }
            }
        }

        eventLogger.track(
            eventName = "complete_login",
            distinctId = user.id.toString(),
            properties = mapOf(
                "provider" to "github",
                "user_id" to user.id,
                "is_new_user" to isNewUser,
            ),
        )

        return tokenManager.createToken(user).withType()
    }
}
