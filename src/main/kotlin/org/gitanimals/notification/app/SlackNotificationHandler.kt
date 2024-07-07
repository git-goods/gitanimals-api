package org.gitanimals.notification.app

import org.gitanimals.notification.app.event.NewUserCreated
import org.gitanimals.notification.domain.Notification
import org.rooftop.netx.api.SagaStartEvent
import org.rooftop.netx.api.SagaStartListener
import org.rooftop.netx.api.SuccessWith
import org.rooftop.netx.meta.SagaHandler
import org.springframework.beans.factory.annotation.Qualifier

@SagaHandler
class SlackNotificationHandler(
    @Qualifier("gitAnimalsNewUserSlackNotification") private val notification: Notification,
) {

    @SagaStartListener(event = NewUserCreated::class, successWith = SuccessWith.END)
    fun handleNewUserCreatedEvent(sagaStartEvent: SagaStartEvent) {
        val newUserCreated = sagaStartEvent.decodeEvent(NewUserCreated::class)

        notification.notify(
            ":kissing_smiling_eyes: 새로운 유저 \"${newUserCreated.username}\" 가 가입했어요 :kissing_smiling_eyes:"
        )
    }
}
