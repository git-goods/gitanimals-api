package org.gitanimals.auction.app

import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductService
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class GetProductFacade(
    private val identityApi: IdentityApi,
    private val productService: ProductService,
) {
    fun getProductsByToken(token: String, pageNumber: Int, count: Int): Page<Product> {
        val user = identityApi.getUserByToken(token)

        return productService.getProductsByUserId(user.id.toLong(), pageNumber, count)
    }
}
