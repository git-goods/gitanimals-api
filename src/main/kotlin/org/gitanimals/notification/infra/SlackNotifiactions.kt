package org.gitanimals.notification.infra

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import org.gitanimals.notification.domain.Notification
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

sealed class SlackNotification(
    token: String,
    private val channel: String,
) : Notification {

    private val slack = Slack.getInstance().methods(token)

    override fun notify(message: String) {
        val request: ChatPostMessageRequest = ChatPostMessageRequest.builder()
            .channel(channel)
            .text(message)
            .build();
        slack.chatPostMessage(request)
    }
}

@Component
class GitAnimalsNewUserSlackNotification(
    @Value(value = "\${slack.token}") token: String,
) : SlackNotification(token, "C079NJ6PVBQ")

@Component
class GitAnimalsDailyReportSlackNotification(
    @Value(value = "\${slack.token}") token: String,
): SlackNotification(token, "C07BPB42R8D")

@Component
class GitAnimalsNewQuizCreatedSlackNotification(
    @Value(value = "\${slack.token}") token: String,
): SlackNotification(token, "C08GU67NV6W")
