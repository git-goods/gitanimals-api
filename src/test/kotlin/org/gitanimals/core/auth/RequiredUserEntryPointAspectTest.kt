package org.gitanimals.core.auth

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestComponent
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootTest(
    classes = [
        RequiredUserEntryPointAspect::class,
        UserEntryPointValidationExtension::class,
        UserEntryPointValidationExtension.UserEntryPointValidationExtensionBeanInjector::class,
        TestService::class
    ]
)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@DisplayName("RequiredUserEntryPointAspect 클래스의")
internal class RequiredUserEntryPointAspectTest(
    @MockkBean(relaxed = true) private val internalAuth: InternalAuth,
    private val testService: TestService,
) : DescribeSpec({


    describe("validate 메소드는") {

        context("expected가 GITHUB로 설정되어있을때, GITHUB유저가 인입되면") {
            it("메소드 호출을 성공한다") {
                every { internalAuth.getUserEntryPoint(any()) } returns "GITHUB"

                testService.githubOnly() shouldBe "github-ok"
            }
        }

        context("expected가 GITHUB로 설정되어 있을때, APPLE유저가 인입되면") {
            it("IllegalArgumentException 을 던진다") {
                every { internalAuth.getUserEntryPoint(any()) } returns "APPLE"

                shouldThrow<IllegalArgumentException> {
                    testService.githubOnly()
                }
            }
        }

        context("expected가 ANY로 설정되어 있을때, 어떤 유저가 인입되더라도 성공한다") {
            it("always invokes the method") {
                every { internalAuth.getUserEntryPoint(any()) } returns "GITHUB"
                testService.anyEntry() shouldBe "any-ok"

                every { internalAuth.getUserEntryPoint(any()) } returns "APPLE"
                testService.anyEntry() shouldBe "any-ok"
            }
        }
    }
})

@TestComponent
class TestService {

    @RequiredUserEntryPoints(expected = [UserEntryPoint.GITHUB])
    fun githubOnly(): String {
        return "github-ok"
    }

    @RequiredUserEntryPoints(expected = [UserEntryPoint.ANY])
    fun anyEntry(): String {
        return "any-ok"
    }
}
