package org.gitanimals.identity.controller.request

enum class RedirectWhenSuccess(
    val callbackUri: String,
    private val successUri: String,
) {
    HOME(
        "https://api.gitanimals.org/logins/oauth/github/tokens/HOME",
        "https://www.gitanimals.org?jwt={jwt}",
    ),
    ADMIN(
        "https://api.gitanimals.org/logins/oauth/github/tokens/ADMIN",
        "https://admin.gitanimals.org?jwt={jwt}",
    ),
    ;

    fun successUriWithToken(jwt: String): String = successUri.replace("{jwt}", jwt)
}
