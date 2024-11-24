package org.gitanimals.inbox.domain

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional(readOnly = true)
class InboxService(
    private val inboxRepository: InboxRepository,
) {

    fun findAllUnreadByUserId(userId: Long): InboxApplication =
        inboxRepository.findAllUnReadByUserId(userId)

    @Transactional
    fun readAllByUserId(userId: Long) {
        val inboxApplication = inboxRepository.findAllUnReadByUserId(userId)

        inboxApplication.readAll()
    }

    @Transactional
    fun inputInbox(
        userId: Long,
        title: String,
        body: String,
        publisher: String,
        publishedAt: Instant,
    ) {
        val newInbox = Inbox.of(
            userId = userId,
            title = title,
            body = body,
            publisher = publisher,
            publishedAt = publishedAt,
        )

        inboxRepository.save(newInbox)
    }
}
