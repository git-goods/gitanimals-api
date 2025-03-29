package org.gitanimals.notification.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.notification.domain.Notification
import org.gitanimals.quiz.infra.event.SlackReplied
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class SlackRepliedMessageListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    private val notifications: List<Notification>,
) : TraceableMessageListener(redisTemplate, objectMapper) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun onMessage(message: Message) {
        runCatching {
            val slackReplied = objectMapper.readValue(
                redisTemplate.stringSerializer.deserialize(message.body),
                SlackReplied::class.java,
            )

            val notification = notifications.first { it.isCompatible(slackReplied.slackChannel) }

            notification.replyInThread(slackReplied.message, slackReplied.threadTs)
        }.getOrElse {
            logger.error("Cannot reply to thread $message", it)
        }
    }
}
