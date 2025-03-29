package org.gitanimals.notification.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.notification.app.event.NotApprovedQuizCreatedNotification
import org.gitanimals.notification.domain.Notification
import org.gitanimals.notification.domain.Notification.ActionRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class NotApprovedQuizCreatedMessageListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    @Qualifier("gitAnimalsNewQuizCreatedSlackNotification") private val newQuizCreatedNotification: Notification,
    @Value("\${quiz.approve.token}") private val approveToken: String,
) : TraceableMessageListener(redisTemplate, objectMapper) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun onMessage(message: Message) {
        runCatching {
            logger.info("NotApprovedQuizCreatedMessageListener message: $message")
            val notApprovedQuizCreatedNotification = objectMapper.readValue(
                redisTemplate.stringSerializer.deserialize(message.body),
                NotApprovedQuizCreatedNotification::class.java,
            )

            val payloadWhenApprovedButtonClicked = mapOf(
                "clicked" to "APPROVED",
                "sourceKey" to "NOT_APPROVED_QUIZ",
                "approveToken" to approveToken,
                "notApprovedQuizId" to notApprovedQuizCreatedNotification.id,
            )

            val payloadWhenNotApprovedButtonClicked = mapOf(
                "clicked" to "NOT_APPROVED",
                "sourceKey" to "NOT_APPROVED_QUIZ",
                "approveToken" to approveToken,
                "notApprovedQuizId" to notApprovedQuizCreatedNotification.id,
            )

            this.newQuizCreatedNotification.notifyWithActions(
                message = """
                    :in_progress:
                    유사도 검색 룰에 통과하지 못한 퀴즈가 생성 요청되었어요.
                    확인 및 수기 승락이 필요해요.
                    ---생성 요청된 퀴즈 :point_down:---
                    id: ${notApprovedQuizCreatedNotification.id}
                    userId: ${notApprovedQuizCreatedNotification.userId}
                    level: ${notApprovedQuizCreatedNotification.level}
                    category: ${notApprovedQuizCreatedNotification.category}
                    problem: ${notApprovedQuizCreatedNotification.problem}
                    expectedAnswer: ${notApprovedQuizCreatedNotification.expectedAnswer}
                    language: ${notApprovedQuizCreatedNotification.language}
                    ---유사하다고 판단된 퀴즈들 :point_down:---
                    ${notApprovedQuizCreatedNotification.similarityQuizTexts.joinToString("\n---\n")}
                """.trimIndent(),
                actions = listOf(
                    ActionRequest(
                        id = "approve_action",
                        style = "primray",
                        name = "Approve",
                        interaction = payloadWhenApprovedButtonClicked,
                    ),
                    ActionRequest(
                        id = "delete_action",
                        style = "danger",
                        name = "Deny",
                        interaction = payloadWhenNotApprovedButtonClicked,
                    )
                )
            )
        }.onFailure {
            logger.error("Fail to publish not approved quiz created event.", it)
        }
    }
}
