package org.gitanimals.notification.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.notification.app.event.DeadLetterEvent
import org.gitanimals.notification.domain.Notification
import org.gitanimals.notification.domain.Notification.ActionRequest
import org.gitanimals.notification.domain.Notification.ActionRequest.Style.PRIMARY
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class SlackDeadLetterMessageListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    @Qualifier("gitAnimalsDeadLetterSlackNotification") private val notification: Notification,
    @Value("\${relay.approve.token}") private val approveToken: String,
) : TraceableMessageListener(redisTemplate, objectMapper) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun onMessage(message: Message) {
        runCatching {
            val deadLetterEvent = objectMapper.readValue(
                redisTemplate.stringSerializer.deserialize(message.body),
                DeadLetterEvent::class.java,
            )

            val payloadWhenRelayButtonClicked = mapOf(
                "clicked" to "RELAY",
                "sourceKey" to "RELAY_DEAD_LETTER",
                "approveToken" to approveToken,
                "deadLetterId" to deadLetterEvent.deadLetterId
            )

            notification.notifyWithActions(
                message = """
                    :this_is_fine::this_is_fine::this_is_fine::this_is_fine::this_is_fine:
                    DeadLetter
                    ---
                    $deadLetterEvent
                    ---
                """.trimIndent(),
                actions = listOf(
                    ActionRequest(
                        id = "relay_action",
                        name = "Relay",
                        style = PRIMARY,
                        interaction = payloadWhenRelayButtonClicked,
                    )
                ),
            )
        }.onFailure {
            logger.error("Fail to notify dead letter. message: $message", it)
        }
    }
}
