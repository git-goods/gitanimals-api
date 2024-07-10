package org.gitanimals.notification.app

import org.gitanimals.notification.app.event.DailyProductReport
import org.gitanimals.notification.app.event.NewUserCreated
import org.gitanimals.notification.app.event.UserYesterdayReport
import org.gitanimals.notification.domain.Notification
import org.rooftop.netx.api.SagaStartEvent
import org.rooftop.netx.api.SagaStartListener
import org.rooftop.netx.api.SuccessWith
import org.rooftop.netx.meta.SagaHandler
import org.springframework.beans.factory.annotation.Qualifier

@SagaHandler
class SlackNotificationHandler(
    @Qualifier("gitAnimalsNewUserSlackNotification") private val newUserSlackNotification: Notification,
    @Qualifier("gitAnimalsDailyReportSlackNotification") private val dailyReportSlackNotification: Notification,
) {

    @SagaStartListener(event = NewUserCreated::class, successWith = SuccessWith.END)
    fun handleNewUserCreatedEvent(sagaStartEvent: SagaStartEvent) {
        val newUserCreated = sagaStartEvent.decodeEvent(NewUserCreated::class)

        newUserSlackNotification.notify(
            ":kissing_smiling_eyes: 새로운 유저 \"${newUserCreated.username}\" 가 가입했어요 :kissing_smiling_eyes:"
        )
    }

    @SagaStartListener(event = UserYesterdayReport::class, successWith = SuccessWith.END)
    fun handleUserYesterdayReport(sagaStartEvent: SagaStartEvent) {
        val userYesterdayReport = sagaStartEvent.decodeEvent(UserYesterdayReport::class)

        dailyReportSlackNotification.notify(
            """
                :pepefireworks: *${userYesterdayReport.serverName} 서버 ${userYesterdayReport.date.toLocalDate()} daily user reports* :pepefireworks:
                어제 하루동안 가입자 수 : ${userYesterdayReport.yesterdayNewUserCount}
                총 가입자 수 : ${userYesterdayReport.totalUserCount}
            """.trimIndent()
        )
    }

    @SagaStartListener(event = DailyProductReport::class, successWith = SuccessWith.END)
    fun handleDailyProductReport(sagaStartEvent: SagaStartEvent) {
        val dailyProductReport = sagaStartEvent.decodeEvent(DailyProductReport::class)

        dailyReportSlackNotification.notify(
            """
                :pepe: *${dailyProductReport.date.toLocalDate()} daily auction reports* :pepe:
                *어제 올라온 상품 수* : ${dailyProductReport.yesterdaySoldOutCount + dailyProductReport.yesterdayOnSaleCount}
                어제 올라온 판매중인 상품 수 : ${dailyProductReport.yesterdayOnSaleCount}
                어제 판매된 상품 수 : ${dailyProductReport.yesterdaySoldOutCount}
                *총 상품 수* : ${dailyProductReport.totalOnSaleCount + dailyProductReport.totalSoldOutCount}
                총 판매중인 상품 수 : ${dailyProductReport.totalOnSaleCount}
                총 판매된 상품 수 : ${dailyProductReport.totalSoldOutCount}
            """.trimIndent()
        )
    }
}
