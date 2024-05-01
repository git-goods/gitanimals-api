package org.gitanimals.auction.domain

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeGreaterThan
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
        context("lastId, personaType, count 를 입력받으면,") {
            val lastId = 0L
            val personaType = "ALL"
            val count = 10

            it("personaType이 일치하는 ON_SALE 상태의 product를 lastId이후로 count개 반환한다.") {
                val result = productService.getProducts(lastId, personaType, count)

                result.shouldHaveSize(count)
                    .should { products ->
                        products.first().id shouldBeGreaterThan lastId
                    }
            }
        }

        context("personaType이 ALL이 아니면,") {
            val lastId = 0L
            val personaType = "CAT"
            val count = 10
            it("personaType과 일치하는 ON_SALE 상태의 product를 count개 반환한다.") {
                val result = productService.getProducts(lastId, personaType, count)

                result.shouldHaveSize(3)
                    .should { products ->
                        products.first().id shouldBeGreaterThan lastId
                        products.filter { it.persona.personaType != personaType }.shouldBeEmpty()
                    }
            }
        }

        context("lastId가 설정되면,") {
            val lastId = products[products.size - 2].id
            val personaType = "CAT"
            val count = 10
            it("lastId 이후의 products 만 조회한다.") {
                val result = productService.getProducts(lastId, personaType, count)

                result.shouldHaveSize(1)
                    .should { products ->
                        products.first().id shouldBeGreaterThan lastId
                        products.filter { it.persona.personaType != personaType }.shouldBeEmpty()
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
