package org.gitanimals.auction.controller

import org.gitanimals.auction.app.RegisterProductFacade
import org.gitanimals.auction.controller.request.RegisterProductRequest
import org.gitanimals.auction.controller.response.ErrorResponse
import org.gitanimals.auction.controller.response.ProductResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class AuctionController(
    private val registerProductFacade: RegisterProductFacade,
) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/auctions/products")
    fun registerProducts(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestBody registerProductRequest: RegisterProductRequest,
    ): ProductResponse {
        val product = registerProductFacade.registerProduct(
            token,
            registerProductRequest.personaId.toLong(),
            registerProductRequest.price.toLong(),
        )

        return ProductResponse.from(product)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(exception: IllegalArgumentException): ErrorResponse =
        ErrorResponse.from(exception)

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(exception: IllegalStateException): ErrorResponse =
        ErrorResponse.from(exception)

}