package org.gitanimals.auction.controller

import org.gitanimals.auction.app.BuyProductFacade
import org.gitanimals.auction.app.ChangeProductFacade
import org.gitanimals.auction.app.DeleteProductFacade
import org.gitanimals.auction.app.RegisterProductFacade
import org.gitanimals.auction.controller.request.RegisterProductRequest
import org.gitanimals.auction.controller.response.ErrorResponse
import org.gitanimals.auction.controller.response.ProductResponse
import org.gitanimals.auction.domain.request.ChangeProductRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class AuctionController(
    private val changeProductFacade: ChangeProductFacade,
    private val deleteProductFacade: DeleteProductFacade,
    private val buyProductFacade: BuyProductFacade,
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

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/auctions/products/{product-id}")
    fun buyProducts(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("product-id") productId: Long,
    ): ProductResponse {
        val product = buyProductFacade.buyProduct(token, productId)

        return ProductResponse.from(product)
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/auctions/products")
    fun changeProduct(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestBody changeProductRequest: ChangeProductRequest,
    ): ProductResponse {
        val product = changeProductFacade.changeProduct(token, changeProductRequest)

        return ProductResponse.from(product)
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/auctions/products/{product-id}")
    fun deleteProducts(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("product-id") productId: Long,
    ): Map<String, String> =
        mapOf("id" to deleteProductFacade.deleteProduct(token, productId).toString())

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(exception: IllegalArgumentException): ErrorResponse =
        ErrorResponse.from(exception)

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(exception: IllegalStateException): ErrorResponse =
        ErrorResponse.from(exception)

}
