package org.gitanimals.identity.app

interface GithubOauth2Api {

    fun getToken(temporaryToken: String): String

    fun getOauthUsername(token: String): OAuthUserResponse

    class OAuthUserResponse(
        val username: String,
        val id: String,
        val profileImage: String,
    )
}
