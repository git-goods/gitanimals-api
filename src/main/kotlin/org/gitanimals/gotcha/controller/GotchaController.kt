package org.gitanimals.gotcha.controller

import org.gitanimals.gotcha.app.GotchaFacade
import org.gitanimals.gotcha.controller.response.ErrorResponse
import org.gitanimals.gotcha.controller.response.GotchaResponse
import org.gitanimals.gotcha.domain.GotchaType
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class GotchaController(
    private val gotchaFacade: GotchaFacade,
) {

    @PostMapping("/gotchas")
    fun gotcha(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam(name = "type", defaultValue = "DEFAULT") type: String,
    ): GotchaResponse {
        val gotchaType = GotchaType.valueOf(type.uppercase())

        val gotchaResponse = gotchaFacade.gotcha(token, gotchaType)

        checkNotNull(gotchaResponse.id)
        return GotchaResponse(gotchaResponse.id!!, gotchaResponse.name, gotchaResponse.point)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(illegalArgumentException: IllegalArgumentException): ErrorResponse =
        ErrorResponse.from(illegalArgumentException)

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(illegalStateException: IllegalStateException): ErrorResponse =
        ErrorResponse("Oops! Something went wrong.")
}
