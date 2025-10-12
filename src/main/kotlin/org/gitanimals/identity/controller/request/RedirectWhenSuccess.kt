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
        "http://localhost:3000/auth?jwt={jwt}",
    ),
    LOCAL_ADMIN(
        "https://api.gitanimals.org/logins/oauth/github/tokens/LOCAL_ADMIN",
        "http://localhost:5173/auth?jwt={jwt}",
    ),
    APP(
        "https://api.gitanimals.org/logins/oauth/github/tokens/APP",
        "gitanimals://auth?jwt={jwt}",
    ),
    WEB_VIEW(
        "https://api.gitanimals.org/logins/oauth/github/tokens/WEB_VIEW",
        "https://git-animal-client-webview.vercel.app/auth?jwt={jwt}",
    ),
    DEV(
        "https://api.gitanimals.org/logins/oauth/github/tokens/DEV",
        "https://gitanimals-dev.vercel.app/auth/jwt={jwt}",
    ),
    ;

    fun successUriWithToken(jwt: String): String = successUri.replace("{jwt}", jwt)
}
