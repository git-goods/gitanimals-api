package org.gitanimals.auction.app

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import org.gitanimals.auction.AuctionTestRoot
import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import kotlin.time.Duration.Companion.seconds

@SpringBootTest(
    classes = [
        RedisContainer::class,
        MockUserServer::class,
        AuctionTestRoot::class,
        MockRenderServer::class,
        AuctionSagaCapture::class,
    ]
)
@TestPropertySource("classpath:test.properties")
@DisplayName("RegisterProductFacade 클래스의")
internal class RegisterProductFacadeTest(
    private val registerProductFacade: RegisterProductFacade,
    private val mockUserServer: MockUserServer,
    private val mockRenderServer: MockRenderServer,
    private val sagaCapture: AuctionSagaCapture,
    private val productRepository: ProductRepository,
) : DescribeSpec({

    beforeEach {
        sagaCapture.clear()
        productRepository.deleteAll()
    }

    afterEach { productRepository.deleteAll() }

    fun registerProduct(): Product {
        mockUserServer.enqueue200(userResponse)
        mockRenderServer.enqueue200(personaResponse)
        mockRenderServer.enqueue200()

        return registerProductFacade.registerProduct(VALID_TOKEN, PERSONA_ID, PRICE)
    }

    describe("registerProduct 메소드는") {
        context("올바른 token, personaId, price 를 입력받으면,") {
            val expected = Product.of(SELLER_ID, PERSONA_ID, "CAT", 4949, 1000)

            it("Product 를 등록한다.") {
                val product = registerProduct()

                eventually(5.seconds) {
                    sagaCapture.startCountShouldBe(1)
                    sagaCapture.joinCountShouldBe(0)
                    sagaCapture.commitCountShouldBe(1)
                    sagaCapture.rollbackCountShouldBe(0)

                    product.shouldBeEqualToIgnoringFields(
                        expected,
                        Product::id,
                        Product::createdAt,
                        Product::modifiedAt,
                        Product::persona,
                    )
                }
            }
        }

        context("productService 이미 product가 등록되어 있다면,") {
            it("product를 등록하지않고, saga 를 rollback시킨다.") {
                registerProduct()

                shouldThrowWithMessage<IllegalArgumentException>("Already registered personaId \"${PERSONA_ID}\"") {
                    mockUserServer.enqueue200(userResponse)
                    mockRenderServer.enqueue200(personaResponse)
                    mockRenderServer.enqueue200()
                    mockRenderServer.enqueue200()

                    registerProductFacade.registerProduct(VALID_TOKEN, PERSONA_ID, PRICE)
                }

                eventually(5.seconds) {
                    sagaCapture.startCountShouldBe(2)
                    sagaCapture.joinCountShouldBe(0)
                    sagaCapture.commitCountShouldBe(2)
                    sagaCapture.rollbackCountShouldBe(1)
                }
            }
        }
    }

}) {

    private companion object {
        private const val VALID_TOKEN = "VALID_TOKEN"
        private const val SELLER_ID = 1L
        private const val PERSONA_ID = 1L
        private const val PRICE = 1_000L

        private val userResponse = IdentityApi.UserResponse("$SELLER_ID", "devxb", "1000")
        private val personaResponse = RenderApi.PersonaResponse("$PERSONA_ID", "CAT", "4949")
    }
}
