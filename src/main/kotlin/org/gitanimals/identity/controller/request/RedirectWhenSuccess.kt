package org.gitanimals.identity.controller.request

enum class RedirectWhenSuccess(
    val callbackUri: String,
    private val successUri: String,
) {
    HOME(
        "https://api.gitanimals.org/logins/oauth/github/tokens/HOME",
        "https://www.gitanimals.org/auth?jwt={jwt}",
    ),
    ADMIN(
        "https://api.gitanimals.org/logins/oauth/github/tokens/ADMIN",
        "https://admin.gitanimals.org/auth?jwt={jwt}",
    ),
    LOCAL(
        "https://api.gitanimals.org/logins/oauth/github/tokens/LOCAL",
        "http://localhost:3000/auth?jwt={jwt}"
    ),
    LOCAL_ADMIN(
        "https://api.gitanimals.org/logins/oauth/github/tokens/LOCAL_ADMIN",
        "http://localhost:5173/auth?jwt={jwt}"
    )
    ;

    fun successUriWithToken(jwt: String): String = successUri.replace("{jwt}", jwt)
}
