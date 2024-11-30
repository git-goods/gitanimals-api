package org.gitanimals.inbox.domain

import org.gitanimals.inbox.core.IdGenerator
import java.time.Instant

fun inbox(
    userId: Long = 0L,
    title: String = "dummy title",
    body: String = "dummy body",
    publisher: String = "gotcha",
    publishedAt: Instant = Instant.now(),
    type: InboxType = InboxType.INBOX,
    redirectTo: String = "/",
    image: String = "/inboxes/default.png",
    status: InboxStatus = InboxStatus.UNREAD,
): Inbox {
    return Inbox(
        id = IdGenerator.generate(),
        userId = userId,
        publisher = Publisher(
            publisher = publisher,
            publishedAt = publishedAt,
        ),
        readAt = null,
        title = title,
        body = body,
        type = type,
        redirectTo = redirectTo,
        image = image,
        status = status,
    )
}
