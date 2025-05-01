package org.gitanimals.shop.controller

import org.gitanimals.core.auth.RequiredUserEntryPoints
import org.gitanimals.core.auth.UserEntryPoint
import org.gitanimals.shop.app.BuyBackgroundFacade
import org.gitanimals.shop.controller.response.ErrorResponse
import org.gitanimals.shop.controller.request.BuyBackgroundRequest
import org.gitanimals.shop.controller.response.BackgroundResponse
import org.gitanimals.shop.domain.SaleService
import org.gitanimals.shop.domain.SaleType
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class BuySaleController(
    private val saleService: SaleService,
    private val buyBackgroundFacade: BuyBackgroundFacade,
) {

    @GetMapping("/shops/backgrounds")
    fun getBackgrounds(): BackgroundResponse =
        BackgroundResponse.from(saleService.findAllByType(SaleType.BACKGROUND))

    @PostMapping("/shops/backgrounds")
    @RequiredUserEntryPoints([UserEntryPoint.GITHUB])
    fun buyBackground(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestBody buyBackgroundRequest: BuyBackgroundRequest
    ) {
        buyBackgroundFacade.buyBackground(token, buyBackgroundRequest.type)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(exception: IllegalArgumentException): ErrorResponse = ErrorResponse.from(exception)
}
