package org.gitanimals.auction.app

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import org.gitanimals.auction.AuctionTestRoot
import org.gitanimals.notification.app.SlackNotificationHandler
import org.gitanimals.notification.infra.GitAnimalsDailyReportSlackNotification
import org.gitanimals.notification.infra.GitAnimalsNewUserSlackNotification
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import kotlin.time.Duration.Companion.seconds

@SpringBootTest(
    classes = [
        AuctionTestRoot::class,
        RedisContainer::class,
        MockUserServer::class,
        MockRenderServer::class,
        AuctionSagaCapture::class,
    ]
)
@TestPropertySource("classpath:test.properties")
@DisplayName("ProductStatisticSchedule 클래스의")
internal class ProductStatisticScheduleTest(
    private val sagaCapture: AuctionSagaCapture,
    private val productStatisticSchedule: ProductStatisticSchedule,
) : DescribeSpec({

    describe("sendDailyProductReport 메소드는") {
        context("호출되면,") {
            it("DailyProductReport 이벤트를 발송한다.") {
                productStatisticSchedule.sendDailyProductReport()

                eventually(5.seconds) {
                    sagaCapture.startCountShouldBe(1)
                }
            }
        }
    }
}) {
}
