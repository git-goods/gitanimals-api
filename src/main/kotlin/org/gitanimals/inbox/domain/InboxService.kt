package org.gitanimals.inbox.domain

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional(readOnly = true)
class InboxService(
    private val inboxRepository: InboxRepository,
) {

    fun findAllByUserId(userId: Long): InboxApplication {
        val inboxes = inboxRepository.findByUserId(userId)

        return InboxApplication(userId, inboxes)
    }

    @Transactional
    fun readById(userId: Long, id: Long) {
        val inbox = inboxRepository.findByIdAndUserId(id = id, userId = userId)

        inbox?.read()
    }

    @Transactional
    fun inputInbox(
        userId: Long,
        type: InboxType,
        title: String,
        body: String,
        image: String,
        redirectTo: String,
        publisher: String,
        publishedAt: Instant,
    ) {
        val newInbox = Inbox.of(
            userId = userId,
            type = type,
            image = image,
            title = title,
            body = body,
            publisher = publisher,
            publishedAt = publishedAt,
            redirectTo = redirectTo,
        )

        inboxRepository.save(newInbox)
    }
}