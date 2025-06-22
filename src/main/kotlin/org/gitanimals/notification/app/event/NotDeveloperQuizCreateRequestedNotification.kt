package org.gitanimals.notification.app.event

data class NotDeveloperQuizCreateRequestedNotification(
    val language: String,
    val category: String,
    val problem: String,
    val traceId: Long,
)
