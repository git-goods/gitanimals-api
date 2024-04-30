package org.gitanimals.auction.domain

import org.gitanimals.auction.domain.request.ChangeProductRequest
import org.gitanimals.auction.domain.request.RegisterProductRequest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.PessimisticLockingFailureException
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
    @Retryable(
        retryFor = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = Int.MAX_VALUE,
    )
    fun buyProduct(productId: Long, buyerId: Long): Product {
        val product = getProductById(productId)

        product.buy(buyerId)

        return product
    }

    @Transactional
    @Retryable(
        retryFor = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = Int.MAX_VALUE,
    )
    fun rollbackProduct(productId: Long): Product {
        val product = getProductById(productId)

        product.onSales()

        return product
    }

    @Transactional
    @Retryable(retryFor = [PessimisticLockingFailureException::class], maxAttempts = 5)
    fun deleteProduct(sellerId: Long, productId: Long): Long {
        val product = getProductByIdWithXForce(productId)

        require(product.sellerId == sellerId) { "Cannot delete product cause your not seller." }
        require(product.getProductState() == ProductState.WAIT_DELETE) {
            "Cannot delete product cause product state is not \"WAIT_DELETE\""
        }

        productRepository.delete(product)

        return product.id
    }

    @Transactional
    @Retryable(
        retryFor = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = Int.MAX_VALUE
    )
    fun waitDeleteProduct(sellerId: Long, productId: Long): Product {
        val product = getProductById(productId)

        product.waitDelete()
        return product
    }

    @Transactional
    fun changeProduct(id: Long, changeProductRequest: ChangeProductRequest): Product {
        val product = getProductById(changeProductRequest.id)

        product.changePrice(changeProductRequest.price)

        return product
    }

    fun getProductByIdWithXForce(productId: Long): Product {
        return productRepository.findByIdWithXForce(productId)
            ?: throw IllegalArgumentException("cannot find matched product by id \"$productId\"")
    }

    fun getProductById(productId: Long): Product =
        productRepository.findByIdOrNull(productId)
            ?: throw IllegalArgumentException("Cannot find matched product by id \"$productId\"")
}
