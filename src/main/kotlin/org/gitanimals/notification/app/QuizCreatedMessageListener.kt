package org.gitanimals.notification.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.notification.app.event.NewQuizCreatedNotification
import org.gitanimals.notification.domain.Notification
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class QuizCreatedMessageListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    @Qualifier("gitAnimalsNewQuizCreatedSlackNotification") private val newQuizCreatedNotification: Notification,
) : TraceableMessageListener(redisTemplate, objectMapper) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun onMessage(message: Message) {
        runCatching {
            val newQuizCreatedNotification = objectMapper.readValue(
                redisTemplate.stringSerializer.deserialize(message.body),
                NewQuizCreatedNotification::class.java,
            )

            this.newQuizCreatedNotification.notify(
                """
                    :pepefireworks::pepefireworks::pepefireworks::pepefireworks::pepefireworks:
                    새로운 퀴즈가 생성되었어요.
                    id: ${newQuizCreatedNotification.id}
                    userId: ${newQuizCreatedNotification.userId}
                    level: ${newQuizCreatedNotification.level}
                    category: ${newQuizCreatedNotification.category}
                    problem: ${newQuizCreatedNotification.problem}
                    expectedAnswer: ${newQuizCreatedNotification.expectedAnswer}
                """.trimIndent()
            )
        }.onFailure {
            logger.error("Fail to publish new quiz created event.", it)
        }
    }
}
