package org.gitanimals.identity.controller

import org.gitanimals.identity.app.LoginFacade
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class Oauth2Controller(
    @Value("\${oauth.client.id.github}") private val githubClientId: String,
    private val loginFacade: LoginFacade,
) {

    @GetMapping("/logins/oauth/github")
    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    fun redirectToGithub(): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .header(
                "Location",
                "https://github.com/login/oauth/authorize?client_id=$githubClientId"
            )
            .build()
    }

    @GetMapping("/logins/oauth/github/tokens")
    @ResponseStatus(HttpStatus.CREATED)
    fun login(
        @RequestParam("code") code: String,
    ): ResponseEntity<Unit> {
        val token = loginFacade.login(code)
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
            .header(
                "Location",
                "http://localhost:3000/jwt?jwt=$token"
            )
            .build()
    }
}
