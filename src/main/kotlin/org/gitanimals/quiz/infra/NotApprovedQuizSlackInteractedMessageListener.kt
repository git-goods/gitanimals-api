package org.gitanimals.quiz.infra

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.core.redis.TraceableMessageListener
import org.gitanimals.quiz.app.ApproveQuizFacade
import org.gitanimals.quiz.app.DenyQuizFacade
import org.gitanimals.quiz.infra.event.SlackInteracted
import org.gitanimals.quiz.infra.event.SlackReplied
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class NotApprovedQuizSlackInteractedMessageListener(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    private val approveQuizFacade: ApproveQuizFacade,
    private val denyQuizFacade: DenyQuizFacade,
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
            val approveToken = payload["approveToken"] as String
            val notApproveQuizId = payload["notApprovedQuizId"] as Long
            val slackChannel = slackInteracted.slackChannel
            val threadTs = slackInteracted.threadTs

            when (clicked) {
                "APPROVED" -> approveQuizFacade.approveQuiz(
                    approveToken = approveToken,
                    notApprovedQuizId = notApproveQuizId,
                )

                "NOT_APPROVED" -> denyQuizFacade.notApprovedQuiz(
                    approveToken = approveToken,
                    notApprovedQuizId = notApproveQuizId,
                )
            }

            eventPublisher.publishEvent(
                SlackReplied(
                    slackChannel = slackChannel,
                    threadTs = threadTs,
                    message = "Quiz $clicked by ${slackInteracted.username}",
                )
            )
        }.onFailure {
            logger.error("Fail to publish new quiz created event.", it)
        }
    }

    companion object {
        private const val SOURCE_KEY = "NOT_APPROVED_QUIZ"
    }
}
