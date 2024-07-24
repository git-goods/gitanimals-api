package org.gitanimals.auction.controller.response

data class TotalProductResponse(
    val count: String
) {

    companion object {
        fun from(totalProductCount: Long): TotalProductResponse =
            TotalProductResponse(totalProductCount.toString())
    }
}
