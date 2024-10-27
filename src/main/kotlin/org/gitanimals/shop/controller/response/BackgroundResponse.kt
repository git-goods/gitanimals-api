package org.gitanimals.shop.controller.response

import org.gitanimals.shop.domain.Sale

data class BackgroundResponse(
    val backgrounds: List<Background>,
) {

    data class Background(
        val type: String,
        val price: String,
    )

    companion object {
        fun from(sales: List<Sale>): BackgroundResponse {
            return BackgroundResponse(
                sales.map {
                    Background(
                        type = it.item,
                        price = it.price.toString(),
                    )
                }.toList()
            )
        }
    }
}
