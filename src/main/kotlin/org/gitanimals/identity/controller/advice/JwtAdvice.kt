package org.gitanimals.identity.controller.advice

import io.jsonwebtoken.security.SignatureException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class JwtAdvice {

    @ExceptionHandler(SignatureException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleSignatureException(signatureException: SignatureException): ErrorResponse =
        ErrorResponse.from(signatureException)
}
