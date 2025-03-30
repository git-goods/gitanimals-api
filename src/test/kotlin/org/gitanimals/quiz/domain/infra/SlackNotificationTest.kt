package org.gitanimals.quiz.domain.infra

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.StringSpec
import org.gitanimals.notification.domain.Notification
import org.gitanimals.notification.infra.GitAnimalsNewQuizCreatedSlackNotification


internal class SlackNotificationTest : StringSpec({

    val objectMapper = ObjectMapper()

    val slackNotificationWithToken = GitAnimalsNewQuizCreatedSlackNotification(
        token = "xoxb-",
        objectMapper = objectMapper,
    )

    val approvedToken = "???"

    "SlackNotification- notifyWithActionTest".config(enabled = false) {
        // given
        val whenApprovedButtonClicked = mapOf(
            "clicked" to "APPROVED",
            "sourceKey" to "NOT_APPROVED_QUIZ",
            "approveToken" to approvedToken,
            "notApprovedQuizId" to 1
        )
        val whenNotApprovedButtonClicked = mapOf(
            "clicked" to "NOT_APPROVED",
            "sourceKey" to "NOT_APPROVED_QUIZ",
            "approveToken" to approvedToken,
            "notApprovedQuizId" to 1
        )

        // when
        slackNotificationWithToken.notifyWithActions(
            message = "xb test",
            actions = listOf(
                Notification.ActionRequest(
                    id = "approve_action",
                    style = Notification.ActionRequest.Style.PRIMARY,
                    name = "Approve",
                    interaction = whenApprovedButtonClicked,
                ),
                Notification.ActionRequest(
                    id = "delete_action",
                    style = Notification.ActionRequest.Style.DANGER,
                    name = "Deny",
                    interaction = whenNotApprovedButtonClicked,
                )
            ),
        )
    }
})
