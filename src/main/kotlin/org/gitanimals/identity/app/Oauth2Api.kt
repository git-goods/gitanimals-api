package org.gitanimals.identity.app

interface Oauth2Api {

    fun getToken(temporaryToken: String): String

    fun getOauthUsername(token: String): OAuthUserResponse

    class OAuthUserResponse(
        val username: String,
        val profileImage: String,
    )
}
