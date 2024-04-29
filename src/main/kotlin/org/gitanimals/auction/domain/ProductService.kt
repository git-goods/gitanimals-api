package org.gitanimals.auction.domain

import org.gitanimals.auction.domain.request.RegisterProductRequest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.annotation.Retryable
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

    @Transactional
    @Retryable(ObjectOptimisticLockingFailureException::class, maxAttempts = Int.MAX_VALUE)
    fun buyProduct(productId: Long, buyerId: Long): Product {
        val product = getProductById(productId)

        product.buy(buyerId)

        return product
    }

    @Transactional
    @Retryable(ObjectOptimisticLockingFailureException::class, maxAttempts = Int.MAX_VALUE)
    fun rollbackProduct(productId: Long): Product {
        val product = getProductById(productId)

        product.onSales()

        return product
    }

    fun getProductById(productId: Long): Product =
        productRepository.findByIdOrNull(productId)
            ?: throw IllegalArgumentException("Cannot find matched product by id \"$productId\"")
}
