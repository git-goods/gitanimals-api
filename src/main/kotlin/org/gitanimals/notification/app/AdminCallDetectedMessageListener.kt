package org.gitanimals.notification.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.notification.app.event.AdminCallDetected
import org.gitanimals.notification.domain.Notification
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class AdminCallDetectedMessageListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    @Qualifier("gitAnimalsAdminCallDetectNotification") private val notification: Notification,
) : TraceableMessageListener(redisTemplate, objectMapper) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun onMessage(message: Message) {
        runCatching {
            val adminCallDetected = objectMapper.readValue(
                redisTemplate.stringSerializer.deserialize(message.body),
                AdminCallDetected::class.java,
            )

            notification.notify(
                message = """
                    _*Admin Call Detected*_
                    *username*: ${adminCallDetected.username},
                    *reason*: ${adminCallDetected.reason},
                    *path*: ${adminCallDetected.path},
                    *description*: ${adminCallDetected.description}
                """.trimIndent(),
            )
        }.onFailure {
            logger.error("Fail to notify dead letter. message: $message", it)
        }
    }
}
