package org.gitanimals.auction.domain

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.should
import org.gitanimals.auction.core.IdGenerator
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import kotlin.random.Random

@DataJpaTest
@DisplayName("ProductService 클래스의")
@TestPropertySource("classpath:test.properties")
@ContextConfiguration(classes = [ProductService::class])
@EntityScan(basePackages = ["org.gitanimals.auction.domain"])
@EnableJpaRepositories(basePackages = ["org.gitanimals.auction.domain"])
internal class ProductServiceTest(
    private val productService: ProductService,
    private val productRepository: ProductRepository,
) : DescribeSpec({

    val products = productRepository.saveAllAndFlush(products)

    describe("getProducts 메소드는") {
        context("pageNumber, personaType, count, sortDirection, orderType 를 입력받으면,") {
            val pageNumber = 0
            val personaType = "ALL"
            val count = 10
            val orderType = "PRICE"
            val sortDirection = "DESC"

            it("personaType이 일치하는 ON_SALE 상태의 product를 count개 반환한다.") {
                val result = productService.getProducts(
                    pageNumber,
                    personaType,
                    count,
                    orderType,
                    sortDirection,
                )

                result.shouldHaveSize(count)
                    .should { products ->
                        products.forEach { it.getState() shouldBeEqual ProductState.ON_SALE }
                    }
            }
        }

        context("personaType이 ALL이 아니면,") {
            val pageNumber = 0
            val personaType = "CAT"
            val count = 10
            val orderType = "LEVEL"
            val sortDirection = "DESC"

            it("personaType과 일치하는 ON_SALE 상태의 product를 count개 반환한다.") {
                val result = productService.getProducts(
                    pageNumber,
                    personaType,
                    count,
                    orderType,
                    sortDirection
                )

                result.shouldHaveSize(3)
                    .should { products ->
                        products.filter { it.persona.personaType != personaType }.shouldBeEmpty()
                    }
            }
        }
    }

    describe("getProductsByUserId 메소드는") {
        context("userId가 입력되면,") {
            val userId = products[0].sellerId
            val pageNumber = 0
            val count = 10
            val orderType = "CREATED_AT"
            val sortDirection = "ASC"

            it("userId에 해당하는 seller의 Products를 모두 반환한다.") {
                val result = productService.getProductsByUserId(
                    userId,
                    pageNumber,
                    count,
                    orderType,
                    sortDirection,
                )

                result.shouldHaveSize(1)
                    .should { products ->
                        products.first().sellerId shouldBeEqual userId
                    }
            }
        }
    }
}) {
    companion object {
        private val products = listOf(
            randomProduct("PENGUIN"),
            randomProduct("PENGUIN"),
            randomProduct("LITTLE_CHICK"),
            randomProduct("LITTLE_CHICK"),
            randomProduct("LITTLE_CHICK"),
            randomProduct("LITTLE_CHICK"),
            randomProduct("LITTLE_CHICK"),
            randomProduct("LITTLE_CHICK"),
            randomProduct("SLIME_RED"),
            randomProduct("SLIME_RED"),
            randomProduct("PIG"),
            randomProduct("PIG"),
            randomProduct("CAT"),
            randomProduct("CAT"),
            randomProduct("CAT"),
        )

        private fun randomProduct(personaType: String): Product {
            return Product.of(
                sellerId = IdGenerator.generate(),
                personaId = IdGenerator.generate(),
                personaType = personaType,
                personaLevel = Random.nextInt(0, Int.MAX_VALUE),
                price = Random.nextLong(1, Long.MAX_VALUE),
            )
        }
    }
}
