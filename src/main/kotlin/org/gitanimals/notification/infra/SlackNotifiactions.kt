package org.gitanimals.notification.infra

import com.fasterxml.jackson.databind.ObjectMapper
import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.model.block.ActionsBlock
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.element.ButtonElement
import org.gitanimals.notification.domain.Notification
import org.gitanimals.notification.domain.Notification.ActionRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

sealed class SlackNotification(
    token: String,
    private val channel: String,
    private val objectMapper: ObjectMapper,
) : Notification {

    private val slack = Slack.getInstance().methods(token)

    override fun isCompatible(channel: String) = this.channel == channel

    override fun notify(message: String) {
        val request: ChatPostMessageRequest = ChatPostMessageRequest.builder()
            .channel(channel)
            .text(message)
            .build();
        slack.chatPostMessage(request)
    }

    override fun notifyWithActions(
        message: String,
        actions: List<ActionRequest>,
    ) {
        val layoutAction: List<LayoutBlock> = listOf(
            SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(message).build())
                .build(),

            ActionsBlock.builder()
                .elements(
                    actions.map {
                        ButtonElement.builder()
                            .text(PlainTextObject.builder().text(it.name).emoji(true).build())
                            .style(it.style)
                            .actionId(it.id)
                            .value(objectMapper.writeValueAsString(it.interaction))
                            .build()
                    }
                ).build()
        )

        val request = ChatPostMessageRequest.builder()
            .channel(channel)
            .blocks(layoutAction)
            .text(message)
            .build()

        slack.chatPostMessage(request)
    }

    override fun replyInThread(message: String, threadTs: String) {
        val request: ChatPostMessageRequest = ChatPostMessageRequest.builder()
            .channel(channel)
            .text(message)
            .threadTs(threadTs)
            .build()

        slack.chatPostMessage(request)
    }
}

@Component
class GitAnimalsNewUserSlackNotification(
    objectMapper: ObjectMapper,
    @Value(value = "\${slack.token}") token: String,
) : SlackNotification(token, "C079NJ6PVBQ", objectMapper)

@Component
class GitAnimalsDailyReportSlackNotification(
    objectMapper: ObjectMapper,
    @Value(value = "\${slack.token}") token: String,
) : SlackNotification(token, "C07BPB42R8D", objectMapper)

@Component
class GitAnimalsNewQuizCreatedSlackNotification(
    objectMapper: ObjectMapper,
    @Value(value = "\${slack.token}") token: String,
) : SlackNotification(token, "C08GU67NV6W", objectMapper)
