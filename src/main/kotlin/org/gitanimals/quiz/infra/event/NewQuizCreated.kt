package org.gitanimals.quiz.infra.event

import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.core.redis.AsyncRedisPubSubEvent
import org.gitanimals.core.redis.RedisPubSubChannel
import org.gitanimals.quiz.domain.Category
import org.gitanimals.quiz.domain.Quiz
import org.slf4j.MDC

data class NewQuizCreated(
    val id: Long,
    val userId: Long,
    val problem: String,
    val level: String,
    val category: Category,
    val expectedAnswer: String,
) : AsyncRedisPubSubEvent(
    channel = RedisPubSubChannel.NEW_QUIZ_CREATED,
    traceId = runCatching { MDC.get(TRACE_ID) }
        .getOrElse { IdGenerator.generate().toString() },
) {

    companion object {
        fun from(entity: Quiz): NewQuizCreated {
            return NewQuizCreated(
                id = entity.id,
                userId = entity.userId,
                problem = entity.problem,
                level = entity.level.name,
                category = entity.category,
                expectedAnswer = entity.expectedAnswer,
            )
        }
    }
}
