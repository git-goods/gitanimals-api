package org.gitanimals.supports.deadletter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.supports.event.SlackInteracted
import org.gitanimals.supports.event.SlackReplied
import org.rooftop.netx.api.DeadLetterRelay
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class DeadLetterRelayEventListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    private val deadLetterRelay: DeadLetterRelay,
    private val applicationEventPublisher: ApplicationEventPublisher,
    @Value("\${relay.approve.token}") private val approveToken: String,
) : TraceableMessageListener(redisTemplate, objectMapper) {

    override fun onMessage(message: Message) {
        runCatching {
            val slackInteracted = objectMapper.readValue(
                redisTemplate.stringSerializer.deserialize(message.body),
                SlackInteracted::class.java
            )

            if (slackInteracted.sourceKey != SOURCE_KEY) {
                return
            }

            val payload = objectMapper.readValue(
                slackInteracted.payload,
                object : TypeReference<Map<String, Any>>() {}
            )

            val approveToken = payload["approveToken"] as String
            val deadLetterId = payload["deadLetterId"] as String

            if (approveToken != this.approveToken) {
                return
            }

            deadLetterRelay.relaySync(deadLetterId)

            applicationEventPublisher.publishEvent(
                SlackReplied(
                    slackChannel = slackInteracted.slackChannel,
                    threadTs = slackInteracted.threadTs,
                    message = "Relay by ${slackInteracted.username}"
                )
            )
        }
    }

    companion object {
        private const val SOURCE_KEY = "RELAY_DEAD_LETTER"
    }
}
