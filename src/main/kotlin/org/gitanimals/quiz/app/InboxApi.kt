package org.gitanimals.quiz.app

import org.gitanimals.inbox.domain.InboxType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.PostExchange
import java.time.Instant

fun interface InboxApi {

    @PostExchange("/internals/inboxes")
    fun inputInbox(@RequestBody request: InboxInputRequest)

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
}
