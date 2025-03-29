package org.gitanimals.notification.app.event

data class NotApprovedQuizCreatedNotification(
    val id: Long,
    val userId: Long,
    val problem: String,
    val level: String,
    val category: String,
    val expectedAnswer: String,
    val traceId: String,
    val language: String,
    val similarityQuizTexts: List<String>,
)
