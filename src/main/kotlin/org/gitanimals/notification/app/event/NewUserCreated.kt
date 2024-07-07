package org.gitanimals.notification.app.event
data class NewUserCreated(
    val userId: Long,
    val username: String,
    val newUserCreated: Boolean,
)
