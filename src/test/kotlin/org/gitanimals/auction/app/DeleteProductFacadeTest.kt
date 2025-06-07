package org.gitanimals.auction.app

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.gitanimals.auction.AuctionTestRoot
import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductRepository
import org.gitanimals.auction.domain.ProductState
import org.gitanimals.core.IdGenerator
import org.gitanimals.core.auth.UserEntryPoint
import org.gitanimals.core.filter.MDCFilter
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ID
import org.slf4j.MDC
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.TestPropertySource
import kotlin.time.Duration.Companion.seconds

@SpringBootTest(
    classes = [
        RedisContainer::class,
        AuctionTestRoot::class,
        AuctionSagaCapture::class,
    ]
)
@TestPropertySource("classpath:test.properties")
@DisplayName("DeleteProductFacade 클래스의")
internal class DeleteProductFacadeTest(
    private val deleteProductFacade: DeleteProductFacade,
    private val registerProductFacade: RegisterProductFacade,
    private val sagaCapture: AuctionSagaCapture,
    private val productRepository: ProductRepository,
    @MockkBean(relaxed = true) private val renderApi: RenderApi,
    @MockkBean(relaxed = true) private val identityApi: IdentityApi,
) : DescribeSpec({

    beforeEach {
        sagaCapture.clear()
        MDC.put(TRACE_ID, IdGenerator.generate().toString())
        MDC.put(USER_ID, IdGenerator.generate().toString())
        MDC.put(MDCFilter.USER_ENTRY_POINT, UserEntryPoint.GITHUB.name)
    }

    afterEach { productRepository.deleteAll() }

    fun registerProduct(): Product {
        every { identityApi.getUserByToken(any()) } returns userResponse
        every { renderApi.getPersonaById(any(), any()) } returns personaResponse
        every { renderApi.deletePersonaById(any(), any()) } just Runs

        MDC.put(TRACE_ID, IdGenerator.generate().toString())
        MDC.put(USER_ID, personaResponse.id)
        MDC.put(MDCFilter.USER_ENTRY_POINT, UserEntryPoint.GITHUB.name)
        return registerProductFacade.registerProduct(VALID_TOKEN, PERSONA_ID, PRICE)
    }

    describe("deleteProduct 메소드는") {
        context("token 에 해당하는 판매자의 product가 아직 ON_SALE 상태라면,") {
            val product = registerProduct()

            every { identityApi.getUserByToken(any()) } returns userResponse
            every { renderApi.addPersona(any(), any(), any()) } just Runs

            it("product 를 삭제하고, 판매자에게 상품을 다시 지급한다.") {

                val result = deleteProductFacade.deleteProduct(VALID_TOKEN, product.id)

                result shouldBeEqual product.id
                productRepository.findByIdOrNull(product.id).shouldBeNull()
            }
        }

        context("persona를 추가하는 과정에서 예외가 던저지면,") {
            val product = registerProduct()

            every { identityApi.getUserByToken(any()) } returns userResponse
            every { renderApi.addPersona(any(), any(), any()) } throws IllegalArgumentException("???")
            it("삭제 트랜잭션을 롤백한다.") {
                shouldThrow<IllegalArgumentException> {
                    deleteProductFacade.deleteProduct(VALID_TOKEN, product.id)
                }

                eventually(50.seconds) {
                    productRepository.findByIdOrNull(product.id).shouldNotBeNull()
                        .getState() shouldBeEqual ProductState.ON_SALE
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
