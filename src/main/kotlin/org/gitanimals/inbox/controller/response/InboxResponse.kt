package org.gitanimals.inbox.controller.response

import org.gitanimals.inbox.domain.InboxApplication

data class InboxResponse(
    val inboxes: List<InboxElement>
) {

    data class InboxElement(
        val id: String,
        val title: String,
        val body: String,
    )

    companion object {

        fun from(inboxApplication: InboxApplication): InboxResponse {
            return InboxResponse(
                inboxes = inboxApplication.inboxes.map {
                    InboxElement(
                        id = it.id.toString(),
                        title = it.title,
                        body = it.body,
                    )
                }
            )
        }
    }
}
