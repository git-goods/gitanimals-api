package org.gitanimals.notification.app

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.notification.app.event.SlackInteracted
import org.gitanimals.notification.app.request.SlackInteractRequest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class SlackInteractRequestDispatcher(
    private val objectMapper: ObjectMapper,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun dispatch(slackInteractRequest: SlackInteractRequest) {
        val payload: Map<String, Any> = objectMapper.readValue(
            slackInteractRequest.actions[0].value,
            object : TypeReference<Map<String, Any>>() {},
        )

        eventPublisher.publishEvent(
            SlackInteracted(
                userId = slackInteractRequest.user.id,
                username = slackInteractRequest.user.username,
                sourceKey = runCatching {
                    payload["sourceKey"] as String
                }.getOrElse {
                    throw IllegalArgumentException("Cannot interact slack request. cause cannot find sourcekey from interact message.")
                },
                payload = slackInteractRequest.actions[0].value,
                slackChannel = slackInteractRequest.container.channelId,
                threadTs = slackInteractRequest.container.messageTs,
            )
        )
    }
}
