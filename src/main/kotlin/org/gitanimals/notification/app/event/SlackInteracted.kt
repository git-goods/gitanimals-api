package org.gitanimals.notification.app.event

import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter
import org.gitanimals.core.redis.AsyncRedisPubSubEvent
import org.gitanimals.core.redis.RedisPubSubChannel.SLACK_INTERACTED
import org.slf4j.MDC

data class SlackInteracted(
    val userId: String,
    val slackChannel: String,
    val threadTs: String,
    val username: String,
    val sourceKey: String,
    val payload: String,
) : AsyncRedisPubSubEvent(
    traceId = runCatching { MDC.get(MDCFilter.TRACE_ID) }
        .getOrElse { IdGenerator.generate().toString() },
    channel = SLACK_INTERACTED
)
