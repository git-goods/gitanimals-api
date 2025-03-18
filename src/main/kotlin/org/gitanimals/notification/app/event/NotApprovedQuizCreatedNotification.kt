package org.gitanimals.notification.app.event

import org.gitanimals.quiz.domain.Category

data class NotApprovedQuizCreatedNotification(
    val id: Long,
    val userId: Long,
    val problem: String,
    val level: String,
    val category: Category,
    val expectedAnswer: String,
    val traceId: String,
    val similarityQuizTexts: List<String>,
)
