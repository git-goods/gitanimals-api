package org.gitanimals.inbox.infra.event

import java.time.Instant

data class InboxInputEvent(
    val title: String,
    val inboxData: InboxData,
    val publisher: Publisher,
) {

    data class Publisher(
        val publisher: String,
        val publishedAt: Instant,
    )

    data class InboxData(
        val userId: Long,
        val title: String,
        val body: String,
    )
}
