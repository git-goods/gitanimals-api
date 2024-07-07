package org.gitanimals.notification.app

import io.kotest.core.spec.style.DescribeSpec
import org.gitanimals.notification.app.event.NewUserCreated
import org.gitanimals.notification.infra.GitAnimalsNewUserSlackNotification
import org.junit.jupiter.api.DisplayName
import org.rooftop.netx.api.SagaManager
import org.rooftop.netx.meta.EnableSaga
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@EnableSaga
@DisplayName("SlackNotificationHandler 클래스의")
@ContextConfiguration(
    classes = [
        SlackNotificationHandler::class,
        GitAnimalsNewUserSlackNotification::class,
    ]
)
@TestPropertySource("classpath:test.properties")
internal class SlackNotificationHandlerTest(
    private val sagaManager: SagaManager,
) : DescribeSpec({

    xdescribe("handleNewUserCreatedEvent 클래스는") {
        context("NewUserCreated 이벤트가 실행되었을때,") {
            it("슬랙에 알람을 보낸다.") {
                sagaManager.startSync(NewUserCreated(1L, "devxb", true))

                Thread.sleep(100000)
            }
        }
    }
}) {
}
