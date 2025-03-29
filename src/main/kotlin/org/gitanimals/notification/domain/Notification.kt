package org.gitanimals.notification.domain

interface Notification {

    fun isCompatible(channel: String): Boolean

    fun notify(message: String)

    fun notifyWithActions(
        message: String,
        whenApprovedButtonClicked: Map<String, *>,
        whenNotApprovedButtonClicked: Map<String, *>,
    )

    fun replyInThread(message: String, threadTs: String)
}
