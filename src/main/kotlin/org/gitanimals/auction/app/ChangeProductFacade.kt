package org.gitanimals.auction.app

import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductService
import org.gitanimals.auction.domain.request.ChangeProductRequest
import org.springframework.stereotype.Service

@Service
class ChangeProductFacade(
    private val identityApi: IdentityApi,
    private val productService: ProductService,
) {

    fun changeProduct(token: String, changeProductRequest: ChangeProductRequest): Product {
        val user = identityApi.getUserByToken(token)

        return productService.changeProduct(user.id.toLong(), changeProductRequest)
    }
}
