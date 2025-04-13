package org.gitanimals.gotcha.app

import com.ninjasquad.springmockk.MockkBean
import io.jsonwebtoken.JwtException
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ID
import org.gitanimals.gotcha.app.response.UserResponse
import org.gitanimals.gotcha.domain.GotchaService
import org.gitanimals.gotcha.domain.GotchaType
import org.gitanimals.gotcha.infra.DropRateCache
import org.junit.jupiter.api.DisplayName
import org.rooftop.netx.meta.EnableSaga
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import kotlin.time.Duration.Companion.seconds

@EnableSaga
@DataJpaTest
@ContextConfiguration(
    classes = [
        RedisContainer::class,
        SagaCapture::class,
        GotchaService::class,
        GotchaFacadeV3::class,
        DropRateCache::class,
    ]
)
@EntityScan("org.gitanimals.gotcha")
@EnableJpaRepositories("org.gitanimals.gotcha")
@TestPropertySource("classpath:test.properties")
@DisplayName("GotchaFacadeV3 클래스의")
internal class GotchaFacadeV3Test(
    private val sagaCapture: SagaCapture,
    private val gotchaFacadeV3: GotchaFacadeV3,
    @MockkBean(relaxed = true) private val userApi: UserApi,
    @MockkBean(relaxed = true) private val renderApi: RenderApi,
) : DescribeSpec({

    beforeEach {
        sagaCapture.clear()
        MDC.put(USER_ID, richUser.id)
        MDC.put(TRACE_ID, IdGenerator.generate().toString())
    }

    describe("gotcha 메소드는") {
        context("token에 해당하는 유저와 GotchaType에 해당하는 Gotcha가 존재한다면,") {
            every { userApi.getUserByToken(any()) } returns richUser
            every {
                renderApi.addPersonas(
                    any(),
                    any(),
                )
            } returns listOf(addPersonaResponse)

            it("gotcha를 성공한다.") {
                val gotchaResponse = gotchaFacadeV3.gotcha("token", GotchaType.DEFAULT, 1)

                gotchaResponse.shouldNotBeNull()
            }
        }

        context("token에 해당하는 유저가 충분한 돈을 갖고있지 않다면,") {
            every { userApi.getUserByToken(any()) } returns poorUser

            it("IllegalArgumentException을 던진다.") {
                shouldThrowWithMessage<IllegalArgumentException>("Not enough point \"0\" <= \"1000\"") {
                    gotchaFacadeV3.gotcha("token", GotchaType.DEFAULT, 1)
                }
            }
        }

        context("포인트가 충분한 유저가 Gotcha를 10번 연속 한다면,") {
            every { userApi.getUserByToken(any()) } returns richUser

            every { renderApi.addPersonas(any(), any()) } returns listOf(
                addPersonaResponse,
                addPersonaResponse,
                addPersonaResponse,
                addPersonaResponse,
                addPersonaResponse,
                addPersonaResponse,
                addPersonaResponse,
                addPersonaResponse,
                addPersonaResponse,
                addPersonaResponse,
            )


            it("gotcha를 성공한다.") {
                val gotchaResponse = gotchaFacadeV3.gotcha("token", GotchaType.DEFAULT, 10)

                gotchaResponse.shouldNotBeNull()
            }
        }

        context("addPersona 중에 실패하면,") {
            every { userApi.getUserByToken(any()) } returns richUser
            every {
                renderApi.addPersonas(
                    any(),
                    any(),
                )
            } throws IllegalArgumentException("for test")

            it("point를 다시 증가시킨다.") {
                shouldThrow<IllegalArgumentException> {
                    gotchaFacadeV3.gotcha("token", GotchaType.DEFAULT, 1)
                }

                eventually(5.seconds) {
                    sagaCapture.rollbackCountShouldBe(1)
                }
            }
        }

        context("JwtException이 발생하면,") {
            every { userApi.getUserByToken(any()) } throws JwtException("for test")

            it("10초안에 예외를 반환한다") {
                shouldThrow<JwtException> {
                    gotchaFacadeV3.gotcha("token", GotchaType.DEFAULT, 1)
                }
            }
        }
    }
}) {

    private companion object {
        private val richUser = UserResponse("1", "rich_guy", "100000", "image-url.png")
        private val poorUser = UserResponse("1", "poor_guy", "0", "image-url.png")
        private val addPersonaResponse =
            RenderApi.PersonaResponse("1", "GOOSE", "1", true, "0.1")
    }
}
