package org.gitanimals.auction.domain

import org.gitanimals.auction.domain.request.ChangeProductRequest
import org.gitanimals.auction.domain.request.RegisterProductRequest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.PessimisticLockingFailureException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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
    fun getProducts(
        pageNumber: Int,
        personaType: String,
        count: Int,
        orderType: String,
        sortDirection: String,
    ): Page<Product> {
        validCount(count)

        val page =
            PageRequest.of(
                pageNumber,
                count,
                Sort.by(
                    Sort.Direction.fromString(sortDirection),
                    ProductOrderType.fromString(orderType),
                )
            )

        return productRepository.findAllProducts(personaType, page)
    }

    fun getProductsByUserId(
        userId: Long,
        pageNumber: Int,
        count: Int,
        orderType: String,
        sortDirection: String,
    ): Page<Product> {
        validCount(count)

        val page =
            PageRequest.of(
                pageNumber,
                count,
                Sort.by(
                    Sort.Direction.fromString(sortDirection),
                    ProductOrderType.fromString(orderType),
                )
            )

        return productRepository.findAllProductsByUserId(userId, page)
    }

    fun getProductHistories(pageNumber: Int, personaType: String, count: Int): Page<Product> {
        validCount(count)

        val page = Pageable.ofSize(count).withPage(pageNumber)

        return productRepository.findAllProductHistories(personaType, page)
    }

    private fun validCount(count: Int) {
        require(count <= 100) { "Maximum count must be under 100" }
    }

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
        maxAttempts = 5
    )
    fun waitBuyProduct(productId: Long, buyerId: Long): Product {
        val product = getProductById(productId)

        product.waitBuy(buyerId)

        return product
    }

    @Transactional
    @Retryable(
        retryFor = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = Int.MAX_VALUE,
    )
    fun buyProduct(productId: Long): Product {
        val product = getProductById(productId)

        product.buy()

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
        require(product.getState() == ProductState.WAIT_DELETE) {
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
    fun changeProduct(sellerId: Long, changeProductRequest: ChangeProductRequest): Product {
        val product = getProductById(changeProductRequest.id.toLong())

        require(product.sellerId == sellerId) {
            "Cannot change product price cause yor not seller"
        }

        product.changePrice(changeProductRequest.price.toLong())

        return product
    }

    private fun getProductByIdWithXForce(productId: Long): Product {
        return productRepository.findByIdWithXForce(productId)
            ?: throw IllegalArgumentException("cannot find matched product by id \"$productId\"")
    }

    fun getProductById(productId: Long): Product =
        productRepository.findByIdOrNull(productId)
            ?: throw IllegalArgumentException("Cannot find matched product by id \"$productId\"")

    fun getPersonaTypeResponses(): List<PersonaType> {
        TODO("Not yet implemented")
    }
}
