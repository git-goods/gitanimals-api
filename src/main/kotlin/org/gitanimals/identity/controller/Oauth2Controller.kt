package org.gitanimals.identity.controller

import org.gitanimals.identity.app.AppleLoginFacade
import org.gitanimals.identity.app.GithubLoginFacade
import org.gitanimals.identity.controller.request.AppleLoginRequest
import org.gitanimals.identity.controller.request.RedirectWhenSuccess
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class Oauth2Controller(
    @Value("\${oauth.client.id.github}") private val githubClientId: String,
    private val githubLoginFacade: GithubLoginFacade,
    private val appleLoginFacade: AppleLoginFacade,
) {

    @GetMapping("/logins/oauth/github")
    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    fun redirectToGithub(
        @RequestHeader(
            name = "Redirect-When-Success",
            defaultValue = "HOME"
        ) redirectWhenSuccess: RedirectWhenSuccess,
    ): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .header(
                "Location",
                "https://github.com/login/oauth/authorize?client_id=$githubClientId&redirect_uri=${redirectWhenSuccess.callbackUri}"
            )
            .build()
    }

    @GetMapping("/logins/oauth/github/tokens/{redirect-path}")
    @ResponseStatus(HttpStatus.CREATED)
    fun login(
        @RequestParam("code") code: String,
        @PathVariable("redirect-path") redirectWhenSuccess: RedirectWhenSuccess,
    ): ResponseEntity<Unit> {
        val token = githubLoginFacade.login(code)
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
            .header(
                "Location",
                redirectWhenSuccess.successUriWithToken(token)
            )
            .build()
    }

    @PostMapping("/logins/oauth/apple")
    fun loginWithAppleAndRedirect(
        @RequestHeader(
            name = "Redirect-When-Success",
            defaultValue = "HOME"
        ) redirectWhenSuccess: RedirectWhenSuccess,
        @RequestHeader(name = "Login-Secret") loginSecret: String,
        @RequestBody appleLoginRequest: AppleLoginRequest,
    ): ResponseEntity<Unit> {
        val token = appleLoginFacade.login(
            username = appleLoginRequest.name,
            profileImage = appleLoginRequest.profileImage,
            loginSecret = loginSecret,
        )

        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
            .header("Location", redirectWhenSuccess.successUriWithToken(token))
            .build()
    }
}
