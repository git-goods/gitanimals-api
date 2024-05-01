package org.gitanimals.auction.controller.response

import org.gitanimals.auction.domain.Product

data class ProductsResponse(
    val products: List<ProductResponse>
) {
    companion object {
        fun from(products: List<Product>): ProductsResponse =
            ProductsResponse(products.map { ProductResponse.from(it) })
    }
}
