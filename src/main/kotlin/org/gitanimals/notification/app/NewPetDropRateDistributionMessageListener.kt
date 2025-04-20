package org.gitanimals.notification.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.notification.app.event.NewPetDropRateDistributionEvent
import org.gitanimals.notification.domain.Notification
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class NewPetDropRateDistributionMessageListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    @Qualifier("gitAnimalsReportSlackNotification") private val dailyReportSlackNotification: Notification,
) : TraceableMessageListener(redisTemplate, objectMapper) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun onMessage(message: Message) {
        runCatching {
            val newPetDropRateDistributionEvent = objectMapper.readValue(
                redisTemplate.stringSerializer.deserialize(message.body),
                NewPetDropRateDistributionEvent::class.java,
            )

            dailyReportSlackNotification.notify(
                message = """
                    :pepe: *${newPetDropRateDistributionEvent.type} drop rate distribution reports* :pepe:
                    ${
                        newPetDropRateDistributionEvent.distributions.sortedBy { it.dropRate }
                        .joinToString("\n") { "- ${it.dropRate}: ${it.count}" }
                    }
                """.trimIndent()
            )
        }.onFailure {
            logger.error("[NewPetDropRateDistributionMessageListener] Fail to notify dropRate message: $message", it)
        }
    }
}

