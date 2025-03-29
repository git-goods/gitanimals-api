package org.gitanimals.notification.app.event

data class SlackReplied(
    val slackChannel: String,
    val threadTs: String,
    val traceId: String,
    val message: String,
)
