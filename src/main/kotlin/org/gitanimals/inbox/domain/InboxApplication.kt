package org.gitanimals.inbox.domain

class InboxApplication(
    val userId: Long,
    val inboxes: List<Inbox>,
) {

    fun readAll() {
        inboxes.forEach {
            it.read()
        }
    }
}
