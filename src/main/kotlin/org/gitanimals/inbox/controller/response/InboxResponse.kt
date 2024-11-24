package org.gitanimals.inbox.controller.response

import org.gitanimals.inbox.domain.InboxApplication
import org.gitanimals.inbox.domain.InboxType

data class InboxResponse(
    val inboxes: List<InboxElement>
) {

    data class InboxElement(
        val id: String,
        val image: String,
        val title: String,
        val body: String,
        val redirectTo: String,
        val type: InboxType,
    )

    companion object {

        fun from(inboxApplication: InboxApplication): InboxResponse {
            return InboxResponse(
                inboxes = inboxApplication.inboxes.map {
                    InboxElement(
                        id = it.id.toString(),
                        image = it.image,
                        title = it.title,
                        body = it.body,
                        redirectTo = it.redirectTo,
                        type = it.type,
                    )
                }
            )
        }
    }
}
