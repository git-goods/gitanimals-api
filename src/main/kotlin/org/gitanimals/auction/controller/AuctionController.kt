package org.gitanimals.auction.controller

import org.gitanimals.auction.app.*
import org.gitanimals.auction.controller.request.RegisterProductRequest
import org.gitanimals.auction.controller.response.ErrorResponse
import org.gitanimals.auction.controller.response.PersonaTypeResponses
import org.gitanimals.auction.controller.response.ProductResponse
import org.gitanimals.auction.controller.response.ProductsResponse
import org.gitanimals.auction.domain.PersonaType
import org.gitanimals.auction.domain.ProductService
import org.gitanimals.auction.domain.request.ChangeProductRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class AuctionController(
    private val productService: ProductService,
    private val changeProductFacade: ChangeProductFacade,
    private val deleteProductFacade: DeleteProductFacade,
    private val buyProductFacade: BuyProductFacade,
    private val registerProductFacade: RegisterProductFacade,
    private val getProductFacade: GetProductFacade,
) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/auctions/products")
    fun getProducts(
        @RequestParam(name = "page-number", defaultValue = "1") pageNumber: Int,
        @RequestParam(name = "persona-type", defaultValue = "ALL") personaType: String,
        @RequestParam(name = "count", defaultValue = "8") count: Int,
    ): ProductsResponse {
        val products = productService.getProducts(pageNumber, personaType, count)

        return ProductsResponse.from(products)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/auctions/products/users")
    fun getProducts(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam(name = "page-number", defaultValue = "1") pageNumber: Int,
        @RequestParam(name = "count", defaultValue = "8") count: Int,
    ): ProductsResponse {
        val products = getProductFacade.getProductsByToken(token, pageNumber, count)

        return ProductsResponse.from(products)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/auctions/products/histories")
    fun getHistory(
        @RequestParam(name = "page-number", defaultValue = "1") pageNumber: Int,
        @RequestParam(name = "persona-type", defaultValue = "ALL") personaType: String,
        @RequestParam(name = "count", defaultValue = "8") count: Int,
    ): ProductsResponse {
        val products = productService.getProductHistories(pageNumber, personaType, count)

        return ProductsResponse.from(products)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/auctions/products/types")
    fun getPersonaTypes(): PersonaTypeResponses =
        PersonaTypeResponses.from(PersonaType.entries.toTypedArray())

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
