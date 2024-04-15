package org.gitanimals.identity.app

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.DisplayName

@DisplayName("Token 클래스의")
internal class TokenTest : DescribeSpec({

    describe("from 메소드는") {
        context("bearer ... 으로 구성된 token을 받으면,") {
            val token = "bearer some critical info"
            it("BEARER 타입의 Token을 생성한다.") {
                Token.from(token).type shouldBeEqual Token.Type.BEARER
            }
        }
    }

}) {
}
