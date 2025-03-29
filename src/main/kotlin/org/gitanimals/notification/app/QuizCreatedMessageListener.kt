package org.gitanimals.notification.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.notification.app.event.NewQuizCreatedNotification
import org.gitanimals.notification.domain.Notification
import org.gitanimals.notification.domain.Notification.ActionRequest
import org.gitanimals.notification.domain.Notification.ActionRequest.Style.DANGER
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class QuizCreatedMessageListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    @Qualifier("gitAnimalsNewQuizCreatedSlackNotification") private val newQuizCreatedNotification: Notification,
    @Value("\${quiz.approve.token}") private val approveToken: String,
) : TraceableMessageListener(redisTemplate, objectMapper) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun onMessage(message: Message) {
        runCatching {
            val newQuizCreatedNotification = objectMapper.readValue(
                redisTemplate.stringSerializer.deserialize(message.body),
                NewQuizCreatedNotification::class.java,
            )

            this.newQuizCreatedNotification.notifyWithActions(
                message = """
                    :pepefireworks::pepefireworks::pepefireworks::pepefireworks::pepefireworks:
                    새로운 퀴즈가 생성되었어요.
                    id: ${newQuizCreatedNotification.id}
                    userId: ${newQuizCreatedNotification.userId}
                    level: ${newQuizCreatedNotification.level}
                    category: ${newQuizCreatedNotification.category}
                    problem: ${newQuizCreatedNotification.problem}
                    expectedAnswer: ${newQuizCreatedNotification.expectedAnswer}
                    language: ${newQuizCreatedNotification.language}
                """.trimIndent(),
                actions = listOf(
                    ActionRequest(
                        id = "delete_action",
                        name = "Delete",
                        style = DANGER,
                        interaction = mapOf(
                            "clicked" to "DELETE",
                            "sourceKey" to "DELETE_QUIZ",
                            "approveToken" to approveToken,
                            "deleteQuizId" to newQuizCreatedNotification.id,
                        )
                    )
                )
            )
        }.onFailure {
            logger.error("Fail to publish new quiz created event.", it)
        }
    }
}
