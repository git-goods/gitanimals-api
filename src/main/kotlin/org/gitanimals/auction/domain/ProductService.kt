package org.gitanimals.auction.domain

import org.gitanimals.auction.domain.request.RegisterProductRequest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
) {

    @Transactional
    fun registerProduct(request: RegisterProductRequest): Product {
        val product = Product.of(
            request.sellerId,
            request.personaId,
            request.personaType,
            request.personaLevel,
            request.price,
        )
        return runCatching {
            productRepository.saveAndFlush(product)
        }.getOrElse {
            require(it !is DataIntegrityViolationException) {
                "Already registered personaId \"${request.personaId}\""
            }

            throw IllegalStateException("Cannot register product", it)
        }
    }
}
