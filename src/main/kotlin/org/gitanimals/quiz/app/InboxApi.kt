package org.gitanimals.quiz.app

import org.gitanimals.inbox.domain.InboxType
import org.springframework.web.bind.annotation.PostMapping
import java.time.Instant

fun interface InboxApi {

    @PostMapping("/internals/inboxes")
    fun inputInbox(request: InboxInputRequest)

    data class InboxInputRequest(
        val inboxData: InboxData,
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
}
