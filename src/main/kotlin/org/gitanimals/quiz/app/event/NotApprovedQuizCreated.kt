package org.gitanimals.quiz.app.event

import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter
import org.gitanimals.core.redis.AsyncRedisPubSubEvent
import org.gitanimals.core.redis.RedisPubSubChannel
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.not_approved.NotApprovedQuiz
import org.slf4j.MDC

data class NotApprovedQuizCreated(
    val id: Long,
    val userId: Long,
    val problem: String,
    val level: String,
    val category: Category,
    val expectedAnswer: String,
    val similarityQuizTexts: List<String>,
) : AsyncRedisPubSubEvent(
    channel = RedisPubSubChannel.NOT_APPROVED_QUIZ_CREATED,
    traceId = runCatching { MDC.get(MDCFilter.TRACE_ID) }
        .getOrElse { IdGenerator.generate().toString() },
) {

    companion object {
        fun from(entity: NotApprovedQuiz, similarityQuizTexts: List<String>): NotApprovedQuizCreated {
            return NotApprovedQuizCreated(
                id = entity.id,
                userId = entity.userId,
                problem = entity.problem,
                level = entity.level.name,
                category = entity.category,
                expectedAnswer = entity.expectedAnswer,
                similarityQuizTexts = similarityQuizTexts,
            )
        }
    }
}
