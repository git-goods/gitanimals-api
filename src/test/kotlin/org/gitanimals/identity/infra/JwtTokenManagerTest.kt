package org.gitanimals.identity.infra

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import org.gitanimals.identity.app.Token
import org.gitanimals.identity.app.TokenManager
import org.gitanimals.identity.domain.user
import org.junit.jupiter.api.DisplayName
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DisplayName("JwtTokenManager 클래스의")
@TestPropertySource("classpath:test.properties")
@ContextConfiguration(classes = [JwtTokenManager::class])
internal class JwtTokenManagerTest(
    private val tokenManager: TokenManager
) : DescribeSpec({

    describe("createToken 메소드는") {
        context("user를 받으면,") {
            val user = user()

            it("BEARER타입의 token을 반환한다.") {
                val token = tokenManager.createToken(user)

                token.type shouldBeEqual Token.Type.BEARER
            }
        }
    }

    describe("getUserId 메소드는") {
        context("token을 받으면,") {
            val user = user()
            val token = tokenManager.createToken(user)

            it("user의 id를 반환한다.") {
                val userId = tokenManager.getUserId(token)

                userId shouldBeEqual user.id
            }
        }
    }
})
