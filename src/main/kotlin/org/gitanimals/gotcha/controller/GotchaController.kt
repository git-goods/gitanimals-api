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

        val gotchaResponses = gotchaFacade.gotcha(token, gotchaType, 1)

        return GotchaResponse(
            gotchaResponses[0].name,
            gotchaResponses[0].ratio
        )
    }

    @PostMapping(path = ["/gotchas"], headers = ["ApiVersion=2"])
    fun gotchaV2(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam(name = "type", defaultValue = "DEFAULT") type: String,
        @RequestParam(name = "count", defaultValue = "1") count: Int,
    ): Map<String, List<GotchaResponse>> {
        val gotchaType = GotchaType.valueOf(type.uppercase())

        val gotchaResponses = gotchaFacade.gotcha(token, gotchaType, count)

        return mapOf(
            "gotchaResults" to gotchaResponses.map {
                GotchaResponse(it.name, it.ratio)
            }.toList()
        )
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
