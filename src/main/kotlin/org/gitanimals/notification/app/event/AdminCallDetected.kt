package org.gitanimals.notification.app.event

data class AdminCallDetected(
    val username: String,
    val reason: String,
    val path: String,
    val description: String,
)
