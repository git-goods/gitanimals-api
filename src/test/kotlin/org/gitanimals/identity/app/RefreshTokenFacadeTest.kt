package org.gitanimals.identity.app

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.gitanimals.core.AuthorizationException
import org.gitanimals.core.redis.RedisConfiguration
import org.gitanimals.identity.domain.UserRepository
import org.gitanimals.identity.domain.UserService
import org.gitanimals.identity.domain.user
import org.gitanimals.identity.infra.JwtTokenManager
import org.rooftop.netx.meta.EnableSaga
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@EnableSaga
@DataJpaTest
@ContextConfiguration(
    classes = [
        RedisContainer::class,
        RedisConfiguration::class,
        JwtTokenManager::class,
        UserService::class,
        RefreshTokenFacade::class,
    ]
)
@EntityScan(basePackages = ["org.gitanimals.identity.domain"])
@EnableJpaRepositories(basePackages = ["org.gitanimals.identity.domain"])
@TestPropertySource("classpath:test.properties")
class RefreshTokenFacadeTest(
    private val refreshTokenFacade: RefreshTokenFacade,
    @Value("\${login.secret}") private val loginSecret: String,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager,
) : DescribeSpec({

    afterEach { userRepository.deleteAll() }

    describe("generateRefreshToken 메소드는") {

        context("처음으로 refresh token을 생성하는 유저라면,") {
            val user = userRepository.save(user())
            val token = tokenManager.createToken(user)

            it("생성된 토큰을 응답한다") {
                shouldNotThrowAny {
                    refreshTokenFacade.generateRefreshToken(loginSecret, token.withType())
                }
            }
        }

        context("이미 refresh token이 존재한다면,") {
            val user = userRepository.save(user())
            val token = tokenManager.createToken(user)

            val oldToken = refreshTokenFacade.generateRefreshToken(loginSecret, token.withType())
            it("기존 refresh token을 새로운 refresh token으로 교체한다") {
                val newToken = refreshTokenFacade.generateRefreshToken(loginSecret, token.withType())

                val shouldNotThrowWhenNewToken = shouldNotThrowAny {
                    refreshTokenFacade.getTokenByRefreshToken(loginSecret, newToken)
                }
                val shouldThrowWhenOldToken = shouldThrowExactly<AuthorizationException> {
                    refreshTokenFacade.getTokenByRefreshToken(loginSecret, oldToken)
                }

                shouldNotThrowWhenNewToken::class shouldBe Token::class
                shouldThrowWhenOldToken::class shouldBe AuthorizationException::class
            }
        }
    }
})
