package org.gitanimals.identity.app

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import org.gitanimals.identity.domain.*
import org.gitanimals.identity.infra.JwtTokenManager
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@ContextConfiguration(
    classes = [
        UserService::class,
        GithubLoginFacade::class,
        JwtTokenManager::class,
    ]
)
@DisplayName("UserService 클래스의")
@TestPropertySource("classpath:test.properties")
@EntityScan(basePackages = ["org.gitanimals.identity.domain"])
@EnableJpaRepositories(basePackages = ["org.gitanimals.identity.domain"])
internal class GithubLoginFacadeTest(
    private val githubLoginFacade: GithubLoginFacade,
    private val userRepository: UserRepository,
    @MockkBean(relaxed = true) private val githubOauth2Api: GithubOauth2Api,
    @MockkBean(relaxed = true) private val contributionApi: ContributionApi,
) : DescribeSpec({

    afterEach {
        userRepository.deleteAll()
    }

    describe("login 메소드는") {
        context("entryPoint, authenticationId가 존재하지 않는 유저가 진입했을때,") {
            val username = "xb"
            val entryPoint = EntryPoint.GITHUB
            val authenticationId = "some id"

            every { githubOauth2Api.getOauthUsername(any()) } returns GithubOauth2Api.OAuthUserResponse(
                username = username,
                id = authenticationId,
                profileImage = "https://...",
            )

            it("username이 일치하는 유저가 없다면 새로운 유저를 생성한다") {
                githubLoginFacade.login("hello")

                val result = userRepository.findByNameAndEntryPoint(username, EntryPoint.GITHUB)

                result shouldNotBe null
                result?.findAuthenticationId() shouldBe authenticationId
            }

            it("username이 일치하는 유저가 있다면, authenticationId를 업데이트 한다") {
                userRepository.save(
                    User(
                        id = 1L,
                        points = 0L,
                        profileImage = "",
                        authInfo = UserAuthInfo(EntryPoint.GITHUB, null),
                        pointHistories = mutableListOf(),
                        name = username,
                    )
                )

                githubLoginFacade.login("hello")

                val result = userRepository.findByNameAndEntryPoint(username, EntryPoint.GITHUB)

                result shouldNotBe null
                result?.findAuthenticationId() shouldBe authenticationId
                userRepository.findAll().count() shouldBe 1
            }
        }

        context("entryPoint, authenticationId가 존재하는 유저가 진입했을때") {
            val changeName = "xb"
            val entryPoint = EntryPoint.GITHUB
            val authenticationId = "some id"

            val savedUser = userRepository.save(
                user(
                    name = "before",
                    authenticationId = authenticationId,
                    entryPoint = entryPoint,
                )
            )

            every { githubOauth2Api.getOauthUsername(any()) } returns GithubOauth2Api.OAuthUserResponse(
                username = changeName,
                id = authenticationId,
                profileImage = "https://...",
            )

            it("username이 변경되었다면, username을 맞춰서 변경한다") {
                githubLoginFacade.login("some code")

                val result = userRepository.findByNameAndEntryPoint(changeName, EntryPoint.GITHUB)

                result shouldNotBe null
                result?.findAuthenticationId() shouldBe authenticationId
                result?.getName() shouldBe changeName
                userRepository.findAll().count() shouldBe 1
            }
        }
    }
})
