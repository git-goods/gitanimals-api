package org.gitanimals.quiz.infra

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language
import org.gitanimals.quiz.domain.prompt.rag.QuizCreateRagService
import org.gitanimals.quiz.infra.event.SlackInteracted
import org.gitanimals.quiz.infra.event.SlackReplied
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class NotDeveloperQuizCreateRequestedSlackInteractedMessageListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    private val quizCreateRagService: QuizCreateRagService,
    private val eventPublisher: ApplicationEventPublisher,
) : TraceableMessageListener(redisTemplate, objectMapper) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

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
                object : TypeReference<Map<String, Any>>() {},
            )
            val clicked = payload["clicked"] as String
            val language = payload["language"] as String
            val category = payload["category"] as String
            val problem = payload["problem"] as String
            val slackChannel = slackInteracted.slackChannel
            val threadTs = slackInteracted.threadTs

            when (clicked) {
                "TUNE IS_DEVELOPER_QUIZ" -> quizCreateRagService.createQuizCreateRag(
                    language = Language.valueOf(language),
                    category = Category.valueOf(category),
                    problem = problem,
                    isDevelopQuiz = true,
                )
            }

            eventPublisher.publishEvent(
                SlackReplied(
                    slackChannel = slackChannel,
                    threadTs = threadTs,
                    message = "$clicked by ${slackInteracted.username}",
                )
            )
        }.onFailure {
            logger.error("Fail to publish Not Developer Quiz Create Requested event", it)
        }
    }

    companion object {
        private const val SOURCE_KEY = "NOT_DEVELOPER_QUIZ_REQUESTED"
    }
}
