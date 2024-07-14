package org.gitanimals.auction.controller

import org.gitanimals.auction.controller.response.TotalProductResponse
import org.gitanimals.auction.domain.ProductStatisticService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuctionStatisticController(
    private val productStatisticService: ProductStatisticService,
) {

    @GetMapping("/auctions/statistics/products/total")
    fun getTotalProductCount() =
        TotalProductResponse.from(
            productStatisticService.getProductTotalStatistic().values.sum()
        )
}
