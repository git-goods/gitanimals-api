package org.gitanimals.gotcha.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.DisplayName
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(
    classes = [
        GotchaService::class,
    ]
)
@DisplayName("GotchaService 클래스의")
internal class GotchaServiceTest(
    private val gotchaService: GotchaService,
) : DescribeSpec({

    describe("gotcha 메소드는") {
        context("GotchaType으로 default를 입력받으면,") {
            it("random한 GotchaResponse를 응답한다.") {
                val result = gotchaService.gotcha(1000L, GotchaType.DEFAULT)

                result.shouldNotBeNull()
            }
        }
    }
})
