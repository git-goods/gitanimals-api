package org.gitanimals.auction.controller.response

import org.gitanimals.auction.domain.Product
import org.springframework.data.domain.Page

data class ProductsResponse(
    val products: List<ProductResponse>,
    val pagination: Pagination,
) {
    companion object {
        fun from(products: Page<Product>): ProductsResponse {
            val productResponses = products.asSequence()
                .map { ProductResponse.from(it) }
                .toList()

            val pagination = Pagination(
                totalRecords = products.count(),
                totalPages = products.totalPages,
                currentPage = products.number,
                nextPage = when (products.hasNext()) {
                    true -> products.number + 1
                    false -> null
                },
                prevPage = when (products.hasPrevious()) {
                    true -> products.number - 1
                    false -> null
                }
            )
            return ProductsResponse(productResponses, pagination)
        }
    }

    data class Pagination(
        val totalRecords: Int,
        val currentPage: Int,
        val totalPages: Int,
        val nextPage: Int?,
        val prevPage: Int?,
    )
}
