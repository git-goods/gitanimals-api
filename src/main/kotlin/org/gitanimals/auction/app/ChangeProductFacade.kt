package org.gitanimals.auction.app

import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductService
import org.gitanimals.auction.domain.request.ChangeProductRequest
import org.gitanimals.core.auth.InternalAuth
import org.springframework.stereotype.Service

@Service
class ChangeProductFacade(
    private val internalAuth: InternalAuth,
    private val productService: ProductService,
) {

    fun changeProduct(changeProductRequest: ChangeProductRequest): Product {
        val userId = internalAuth.getUserId()

        return productService.changeProduct(userId, changeProductRequest)
    }
}
