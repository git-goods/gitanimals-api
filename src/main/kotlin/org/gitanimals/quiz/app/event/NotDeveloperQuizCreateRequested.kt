package org.gitanimals.quiz.app.event

import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter
import org.gitanimals.core.redis.AsyncRedisPubSubEvent
import org.gitanimals.core.redis.RedisPubSubChannel
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language
import org.slf4j.MDC

data class NotDeveloperQuizCreateRequested(
    val language: Language,
    val category: Category,
    val problem: String,
) : AsyncRedisPubSubEvent(
    channel = RedisPubSubChannel.NOT_DEVELOPER_QUIZ_CREATED,
    traceId = runCatching { MDC.get(MDCFilter.TRACE_ID) }
        .getOrElse { IdGenerator.generate().toString() },
)
