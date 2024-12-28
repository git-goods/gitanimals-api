package org.gitanimals.inbox.infra

import org.gitanimals.inbox.domain.InboxService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ExpiredInboxDeleteBatch(
    private val inboxService: InboxService,
) {

    @Scheduled(cron = EVERY_10_MINUTES)
    fun deleteExpiredInbox() {
        inboxService.deleteExpiredInboxes()
    }

    companion object {
        private const val EVERY_10_MINUTES = "0 */10 * * * *"
    }
}
