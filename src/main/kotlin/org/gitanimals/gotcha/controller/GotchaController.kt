package org.gitanimals.gotcha.controller

import org.gitanimals.core.auth.RequiredUserEntryPoints
import org.gitanimals.core.auth.UserEntryPoint
import org.gitanimals.gotcha.app.GotchaFacadeV3
import org.gitanimals.gotcha.app.response.GotchaResponseV3
import org.gitanimals.gotcha.controller.response.ErrorResponse
import org.gitanimals.gotcha.domain.GotchaType
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class GotchaController(
    private val gotchaFacadeV3: GotchaFacadeV3,
) {

    @RequiredUserEntryPoints([UserEntryPoint.GITHUB])
    @PostMapping(path = ["/gotchas"], headers = ["Api-Version=3"])
    fun gotchaV3(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam(name = "type", defaultValue = "DEFAULT") type: String,
        @RequestParam(name = "count", defaultValue = "1") count: Int,
    ): Map<String, List<GotchaResponseV3>> {
        val gotchaType = GotchaType.valueOf(type.uppercase())

        val gotchaResponses = gotchaFacadeV3.gotcha(token, gotchaType, count)

        return mapOf("gotchaResults" to gotchaResponses)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(illegalArgumentException: IllegalArgumentException): ErrorResponse =
        ErrorResponse.from(illegalArgumentException)
}
