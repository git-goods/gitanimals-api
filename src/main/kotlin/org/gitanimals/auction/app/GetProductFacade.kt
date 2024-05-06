package org.gitanimals.auction.app

import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductService
import org.springframework.stereotype.Service

@Service
class GetProductFacade(
    private val identityApi: IdentityApi,
    private val productService: ProductService,
) {
    fun getProductsByToken(token: String, lastId: Long, count: Int): List<Product> {
        val user = identityApi.getUserByToken(token)

        return productService.getProductsByUserId(user.id.toLong(), lastId, count)
    }


}
