package org.gitanimals.identity.app

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.DescribeSpec
import org.gitanimals.core.redis.RedisConfiguration
import org.gitanimals.identity.IdentityTestRoot
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import kotlin.time.Duration.Companion.seconds

@SpringBootTest(
    classes = [
        IdentityTestRoot::class,
        RedisContainer::class,
        IdentitySagaCapture::class,
        RedisConfiguration::class,
    ]
)
@TestPropertySource("classpath:test.properties")
internal class UserStatisticScheduleTest(
    private val userStatisticSchedule: UserStatisticSchedule,
    private val identitySagaCapture: IdentitySagaCapture,
) : DescribeSpec({

    describe("sendYesterdayNewUserReport 메소드는") {
        context("호출되면,") {
            it("UserYesterdayReport 를 담은 이벤트를 발행한다.") {
                userStatisticSchedule.sendYesterdayNewUserReport()

                eventually(5.seconds) {
                    identitySagaCapture.startCountShouldBe(1)
                }
            }
        }
    }
})
