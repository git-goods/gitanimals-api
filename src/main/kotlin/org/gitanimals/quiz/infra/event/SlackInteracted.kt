package org.gitanimals.quiz.infra.event

data class SlackInteracted(
    val userId: String,
    val username: String,
    val slackChannel: String,
    val threadTs: String,
    val sourceKey: String,
    val payload: String,
    val traceId: String,
)
