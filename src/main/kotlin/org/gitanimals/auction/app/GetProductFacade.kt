package org.gitanimals.auction.app

import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductService
import org.gitanimals.core.auth.InternalAuth
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class GetProductFacade(
    private val internalAuth: InternalAuth,
    private val productService: ProductService,
) {
    fun getProductsByToken(
        token: String,
        pageNumber: Int,
        count: Int,
        orderType: String,
        sortDirection: String,
    ): Page<Product> {
        val userId = internalAuth.getUserId()

        return productService.getProductsByUserId(
            userId,
            pageNumber,
            count,
            orderType,
            sortDirection,
        )
    }
}
