package org.gitanimals.core.admin

import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter
import org.gitanimals.core.redis.AsyncRedisPubSubEvent
import org.gitanimals.core.redis.RedisPubSubChannel
import org.slf4j.MDC

data class AdminCallDetected(
    val username: String,
    val reason: String,
    val path: String,
    val description: String,
) : AsyncRedisPubSubEvent(
    channel = RedisPubSubChannel.ADMIN_CALL_DETECTED,
    traceId = runCatching { MDC.get(MDCFilter.TRACE_ID) }
        .getOrElse { IdGenerator.generate().toString() },
)
