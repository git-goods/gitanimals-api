package org.gitanimals.identity.domain

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@ContextConfiguration(classes = [UserService::class])
@DisplayName("UserService 클래스의")
@TestPropertySource("classpath:test.properties")
@EntityScan(basePackages = ["org.gitanimals.identity.domain"])
@EnableJpaRepositories(basePackages = ["org.gitanimals.identity.domain"])
internal class UserServiceTest(
    private val userService: UserService,
    private val userRepository: UserRepository,
) : DescribeSpec({

    beforeEach {
        userRepository.saveAndFlush(defaultUser)
    }

    afterEach {
        userRepository.deleteAll()
    }

    describe("givePoint 메소드는") {
        context("username에 해당하는 user가 존재할경우,") {
            val point = 100L
            it("입력받은 point를 지급한다.") {
                userService.givePoint(USER_NAME, point)

                val user = userRepository.findByName(USER_NAME)!!

                user.getPoints() shouldBeEqual point
            }
        }

        context("username에 해당하는 user가 존재하지 않을경우,") {
            it("IllegalArgumentException을 던진다.") {
                shouldThrowExactly<IllegalArgumentException> {
                    userService.givePoint(NOT_EXIST_USER_NAME, 100)
                }
            }
        }
    }

    describe("newUser 메소드는") {
        context("username과 년별 contribution 내역을 받으면,") {
            val username = "NEW_USER"
            val contributionPerYears = mapOf(2024 to 100, 2023 to 100)
            val expectedPoint = 200 * 100L

            it("contribution * 100의 포인트를 갖고있는 새로운 user를 생성한다.") {
                val user = userService.newUser(username, contributionPerYears)

                user.getPoints() shouldBeEqual expectedPoint
            }
        }
    }
}) {

    private companion object {
        private const val NOT_EXIST_USER_NAME = "NOT_EXIST"
        private const val USER_NAME = "NAME"
        private val defaultUser = user(name = USER_NAME)
    }
}
