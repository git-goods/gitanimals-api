package org.gitanimals.notification.domain

fun interface Notification {

    fun notify(message: String)
}
