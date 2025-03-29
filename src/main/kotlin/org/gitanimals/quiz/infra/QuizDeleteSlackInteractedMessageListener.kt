package org.gitanimals.quiz.infra

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.quiz.app.DeleteQuizFacade
import org.gitanimals.quiz.infra.event.SlackInteracted
import org.gitanimals.quiz.infra.event.SlackReplied
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class QuizDeleteSlackInteractedMessageListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    private val deleteQuizFacade: DeleteQuizFacade,
    private val applicationEventPublisher: ApplicationEventPublisher,
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
            val deleteQuizId = payload["deleteQuizId"] as Long

            deleteQuizFacade.deleteQuizById(approveToken, deleteQuizId)

            applicationEventPublisher.publishEvent(
                SlackReplied(
                    slackChannel = slackInteracted.slackChannel,
                    threadTs = slackInteracted.threadTs,
                    message = "Quiz deleted by ${slackInteracted.username}"
                )
            )
        }
    }

    companion object {
        private const val SOURCE_KEY = "DELETE_QUIZ"
    }
}
