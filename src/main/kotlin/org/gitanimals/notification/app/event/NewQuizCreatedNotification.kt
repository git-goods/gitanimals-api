package org.gitanimals.notification.app.event

data class NewQuizCreatedNotification(
    val id: Long,
    val userId: Long,
    val problem: String,
    val level: String,
    val category: String,
    val expectedAnswer: String,
    val traceId: String,
)
