package org.gitanimals.supports.deadletter

import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter
import org.gitanimals.core.redis.AsyncRedisPubSubEvent
import org.gitanimals.core.redis.RedisPubSubChannel.DEAD_LETTER_OCCURRED
import org.rooftop.netx.api.SagaEvent
import org.slf4j.MDC

data class DeadLetterEvent(
    private val deadLetterId: String,
    private val sagaEvent: SagaEvent
): AsyncRedisPubSubEvent(
    traceId = runCatching { MDC.get(MDCFilter.TRACE_ID) }
        .getOrElse { IdGenerator.generate().toString() },
    channel = DEAD_LETTER_OCCURRED,
)
