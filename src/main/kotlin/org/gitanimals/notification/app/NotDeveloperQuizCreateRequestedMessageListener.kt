package org.gitanimals.notification.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.notification.app.event.NotDeveloperQuizCreateRequestedNotification
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
class NotDeveloperQuizCreateRequestedMessageListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    @Qualifier("gitAnimalsNewQuizCreatedSlackNotification") private val newQuizCreatedNotification: Notification,
    @Value("\${quiz.approve.token}") private val approveToken: String,
) : TraceableMessageListener(redisTemplate, objectMapper) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun onMessage(message: Message) {
        runCatching {
            logger.info("NotDeveloperQuizCreateRequestedMessageListener message: $message")
            val notDeveloperQuizCreateRequested = objectMapper.readValue(
                redisTemplate.stringSerializer.deserialize(message.body),
                NotDeveloperQuizCreateRequestedNotification::class.java,
            )

            val payloadWhenApprovedButtonClicked = mapOf(
                "clicked" to "TUNE IS_DEVELOPER_QUIZ",
                "sourceKey" to "NOT_DEVELOPER_QUIZ_REQUESTED",
                "approveToken" to approveToken,
                "category" to notDeveloperQuizCreateRequested.category,
                "language" to notDeveloperQuizCreateRequested.language,
                "problem" to notDeveloperQuizCreateRequested.problem,
            )

            this.newQuizCreatedNotification.notifyWithActions(
                message = """
                    :warning:
                    개발 관련 내용이 아닌 퀴즈가 생성 요청되었어요. 
                    개발 관련 퀴즈라면, 버튼을 눌러 학습 시켜주세요.
                    ---:point_down: 생성 요청된 퀴즈 :point_down:---
                    language: ${notDeveloperQuizCreateRequested.language}
                    category: ${notDeveloperQuizCreateRequested.category}
                    problem: ${notDeveloperQuizCreateRequested.problem}
                """.trimIndent(),
                actions = listOf(
                    ActionRequest(
                        id = "approve_action",
                        style = DANGER,
                        name = "눌러서 학습시키기",
                        interaction = payloadWhenApprovedButtonClicked,
                    ),
                )
            )
        }.onFailure {
            logger.error("Fail to publish not developer quiz create event.", it)
        }
    }
}
