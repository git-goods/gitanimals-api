package org.gitanimals.auction.app

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import org.gitanimals.auction.AuctionTestRoot
import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductRepository
import org.gitanimals.auction.domain.ProductState
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
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
@DisplayName("DeleteProductFacade 클래스의")
internal class DeleteProductFacadeTest(
    private val deleteProductFacade: DeleteProductFacade,
    private val registerProductFacade: RegisterProductFacade,
    private val mockUserServer: MockUserServer,
    private val mockRenderServer: MockRenderServer,
    private val sagaCapture: AuctionSagaCapture,
    private val productRepository: ProductRepository,
) : DescribeSpec({

    beforeEach {
        sagaCapture.clear()
    }

    afterEach { productRepository.deleteAll() }

    fun registerProduct(): Product {
        mockUserServer.enqueue200(userResponse)
        mockRenderServer.enqueue200(personaResponse)
        mockRenderServer.enqueue200()

        return registerProductFacade.registerProduct(VALID_TOKEN, PERSONA_ID, PRICE)
    }

    describe("deleteProduct 메소드는") {
        context("token 에 해당하는 판매자의 product가 아직 ON_SALE 상태라면,") {
            val product = registerProduct()

            mockUserServer.enqueue200(userResponse)
            mockRenderServer.enqueue200()
            it("product 를 삭제하고, 판매자에게 상품을 다시 지급한다.") {

                val result = deleteProductFacade.deleteProduct(VALID_TOKEN, product.id)

                result shouldBeEqual product.id
                productRepository.findByIdOrNull(product.id).shouldBeNull()
            }
        }

        context("persona를 추가하는 과정에서 예외가 던저지면,") {
            val product = registerProduct()

            mockUserServer.enqueue200(userResponse)
            mockRenderServer.enqueue400()
            it("삭제 트랜잭션을 롤백한다.") {
                shouldThrow<IllegalArgumentException> {
                    deleteProductFacade.deleteProduct(VALID_TOKEN, product.id)
                }

                eventually(5.seconds) {
                    productRepository.findByIdOrNull(product.id).shouldNotBeNull()
                        .getProductState() shouldBeEqual ProductState.ON_SALE
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
