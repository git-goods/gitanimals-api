package org.gitanimals.auction.app

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.gitanimals.auction.AuctionTestRoot
import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductRepository
import org.gitanimals.auction.domain.ProductState
import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ID
import org.slf4j.MDC
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.TestPropertySource
import kotlin.time.Duration.Companion.seconds

@SpringBootTest(
    classes = [
        AuctionTestRoot::class,
        RedisContainer::class,
        AuctionSagaCapture::class,
    ]
)
@DisplayName("BuyProductFacade 클래스의")
@TestPropertySource("classpath:test.properties")
internal class BuyProductFacadeTest(
    private val buyProductFacade: BuyProductFacade,
    private val registerProductFacade: RegisterProductFacade,
    private val sagaCapture: AuctionSagaCapture,
    private val productRepository: ProductRepository,
    @MockkBean(relaxed = true) private val renderApi: RenderApi,
    @MockkBean(relaxed = true) private val identityApi: IdentityApi,
) : DescribeSpec({

    beforeEach {
        MDC.put(TRACE_ID, IdGenerator.generate().toString())
        sagaCapture.clear()
    }

    afterEach { productRepository.deleteAll() }

    fun registerProduct(): Product {
        every { identityApi.getUserByToken(any()) } returns userResponse
        every { renderApi.getPersonaById(any(), any()) } returns personaResponse
        every { renderApi.deletePersonaById(any(), any()) } just Runs

        MDC.put(USER_ID, userResponse.id)
        MDC.put(TRACE_ID, IdGenerator.generate().toString())
        return registerProductFacade.registerProduct(VALID_TOKEN, PERSONA_ID, PRICE)
    }

    describe("buyProduct 메소드는") {
        context("토큰에 해당하는 유저가 충분한 돈을 갖고있다면,") {
            val product = registerProduct()

            every { identityApi.getUserByToken(any()) } returns userResponse
            every { identityApi.decreasePoint(any(), any(), any()) } just Runs
            every { identityApi.increasePoint(any(), any(), any()) } just Runs
            every { identityApi.getUserById(any()) } returns userResponse
            every { renderApi.addPersona(any(), any(), any()) } just Runs

            it("product 구매에 성공한다.") {
                val response = buyProductFacade.buyProduct(VALID_TOKEN, product.id)

                eventually(5.seconds) {
                    sagaCapture.startCountShouldBe(2)
                    sagaCapture.joinCountShouldBe(3)
                    sagaCapture.commitCountShouldBe(1)
                    sagaCapture.rollbackCountShouldBe(0)

                    response.getState() shouldBeEqual ProductState.SOLD_OUT
                    response.getBuyerId()!! shouldBeEqual 1L
                    response.getSoldAt().shouldNotBeNull()
                }
            }
        }

        context("토큰에 해당하는 유저가 충분한 돈을 갖고있지 않다면,") {
            val product = registerProduct()

            it("결제를 진행하지 않는다.") {
                every { identityApi.getUserByToken(any()) } returns poorUserResponse

                shouldThrowWithMessage<IllegalArgumentException>(
                    "Cannot buy product cause buyer does not have enough point \"${product.getPrice()}\" >= \"${poorUserResponse.points}\""
                ) { buyProductFacade.buyProduct(VALID_TOKEN, product.id) }

                eventually(5.seconds) {
                    sagaCapture.startCountShouldBe(1)
                    sagaCapture.joinCountShouldBe(0)
                    sagaCapture.commitCountShouldBe(0)
                    sagaCapture.rollbackCountShouldBe(1)
                }
            }
        }

        context("토큰에 해당하는 유저가 결제중에 돈이 부족해졌다면,") {
            val product = registerProduct()

            it("결제를 중단하고, Products의 상태를 ON_SALE로 변경한다.") {
                every { identityApi.getUserByToken(any()) } returns userResponse
                every {
                    identityApi.decreasePoint(
                        any(),
                        any(),
                        any()
                    )
                } throws IllegalArgumentException("")

                shouldThrow<IllegalArgumentException> {
                    buyProductFacade.buyProduct(VALID_TOKEN, product.id)
                }

                eventually(5.seconds) {
                    sagaCapture.startCountShouldBe(1)
                    sagaCapture.joinCountShouldBe(1)
                    sagaCapture.commitCountShouldBe(0)
                    sagaCapture.rollbackCountShouldBe(1)

                    productRepository.findByIdOrNull(product.id)!!
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

        private val poorUserResponse = IdentityApi.UserResponse("$SELLER_ID", "devxb", "999")
        private val userResponse = IdentityApi.UserResponse("$SELLER_ID", "devxb", "1000")
        private val personaResponse = RenderApi.PersonaResponse("$PERSONA_ID", "CAT", "4949")
    }
}
