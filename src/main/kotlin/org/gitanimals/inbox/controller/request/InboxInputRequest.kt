package org.gitanimals.inbox.controller.request

import org.gitanimals.inbox.domain.InboxType
import java.time.Instant

data class InboxInputRequest(
    val inboxData: InboxData,
    val publisher: Publisher,
) {

    data class Publisher(
        val publisher: String,
        val publishedAt: Instant,
    )

    data class InboxData(
        val userId: Long,
        val type: InboxType,
        val title: String,
        val body: String,
        val image: String,
        val redirectTo: String,
    )
}
