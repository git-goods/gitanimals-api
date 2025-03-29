package org.gitanimals.notification.domain

interface Notification {

    fun isCompatible(channel: String): Boolean

    fun notify(message: String)

    fun notifyWithActions(
        message: String,
        actions: List<ActionRequest>,
    )

    fun replyInThread(message: String, threadTs: String)

    data class ActionRequest(
        val id: String,
        val name: String,
        val style: Style,
        val interaction: Map<String, *>
    ) {

        enum class Style {
            PRIMARY,
            DANGER,
            ;
        }
    }
}
