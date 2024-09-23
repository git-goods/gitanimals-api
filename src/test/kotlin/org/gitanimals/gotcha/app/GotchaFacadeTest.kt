package org.gitanimals.gotcha.app

import io.jsonwebtoken.JwtException
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import org.gitanimals.gotcha.GotchaTestRoot
import org.gitanimals.gotcha.app.response.UserResponse
import org.gitanimals.gotcha.domain.GotchaType
import org.gitanimals.gotcha.infra.RestRenderApi
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import kotlin.time.Duration.Companion.seconds

@SpringBootTest(
    classes = [
        GotchaTestRoot::class,
        RedisContainer::class,
        MockUserServer::class,
        MockRenderServer::class,
        SagaCapture::class,
    ]
)
@TestPropertySource("classpath:test.properties")
@DisplayName("GotchaFacade 클래스의")
class GotchaFacadeTest(
    private val sagaCapture: SagaCapture,
    private val gotchaFacade: GotchaFacade,
    private val mockUserServer: MockUserServer,
    private val mockRenderServer: MockRenderServer,
) : DescribeSpec({

    describe("gotcha 메소드는") {
        context("token에 해당하는 유저와 GotchaType에 해당하는 Gotcha가 존재한다면,") {
            mockUserServer.enqueue200(richUser)
            mockUserServer.enqueue200()
            mockRenderServer.enqueue200(listOf(addPersonaResponse))

            it("gotcha를 성공한다.") {
                val gotchaResponse = gotchaFacade.gotcha("token", GotchaType.DEFAULT, 1)

                gotchaResponse.shouldNotBeNull()
            }
        }

        context("token에 해당하는 유저가 충분한 돈을 갖고있지 않다면,") {
            mockUserServer.enqueue200(poorUser)

            it("IllegalArgumentException을 던진다.") {
                shouldThrowWithMessage<IllegalArgumentException>("Not enough point \"0\" <= \"1000\"") {
                    gotchaFacade.gotcha("token", GotchaType.DEFAULT, 1)
                }
            }
        }

        context("포인트가 충분한 유저가 Gotcha를 10번 연속 한다면,") {
            mockUserServer.enqueue200(richUser)
            mockUserServer.enqueue200()
            mockRenderServer.enqueue200(
                (listOf(
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
                ))
            )

            it("gotcha를 성공한다.") {
                val gotchaResponse = gotchaFacade.gotcha("token", GotchaType.DEFAULT, 10)

                gotchaResponse.shouldNotBeNull()
            }
        }

        context("addPersona 중에 실패하면,") {
            mockUserServer.enqueue200(richUser)
            mockUserServer.enqueue200()
            mockRenderServer.enqueue400()
            mockUserServer.enqueue200()

            it("point를 다시 증가시킨다.") {
                shouldThrow<IllegalArgumentException> {
                    gotchaFacade.gotcha("token", GotchaType.DEFAULT, 1)
                }

                eventually(5.seconds) {
                    sagaCapture.rollbackCountShouldBe(1)
                }
            }
        }

        context("JwtException이 발생하면,") {
            mockUserServer.enqueue401()

            it("10초안에 예외를 반환한다") {
                shouldThrow<JwtException> {
                    gotchaFacade.gotcha("token", GotchaType.DEFAULT, 1)
                }
            }
        }
    }
}) {

    private companion object {
        private val richUser = UserResponse("1", "rich_guy", "100000", "image-url.png")
        private val poorUser = UserResponse("1", "poor_guy", "0", "image-url.png")
        private val addPersonaResponse =
            RestRenderApi.PersonaResponse("1", "GOOSE", "1", true, "0.1")
    }
}
