package org.gitanimals.notification.app

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
                :불꽃놀이: ${userYesterdayReport.serverName} 서버의 ${userYesterdayReport.date} daily user reports :불꽃놀이:
                어제 하루동안 가입자 수 : ${userYesterdayReport.yesterdayNewUserCount}
                총 가입자 수 : ${userYesterdayReport.totalUserCount}
            """.trimIndent()
        )
    }
}
