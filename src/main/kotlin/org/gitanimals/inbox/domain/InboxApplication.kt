package org.gitanimals.inbox.domain

import org.gitanimals.inbox.core.AggregateRoot

@AggregateRoot
class InboxApplication(
    val userId: Long,
    val inboxes: List<Inbox>,
)
