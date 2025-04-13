package org.gitanimals.inbox.app

import org.gitanimals.core.auth.InternalAuth
import org.gitanimals.inbox.domain.InboxApplication
import org.gitanimals.inbox.domain.InboxService
import org.springframework.stereotype.Component

@Component
class InboxFacade(
    private val internalAuth: InternalAuth,
    private val inboxService: InboxService,
) {

    fun findAllUnreadByToken(token: String): InboxApplication {
        val userId = internalAuth.getUserId()

        return inboxService.findAllByUserId(userId)
    }

    fun readInboxByTokenAndId(token: String, id: Long) {
        val userId = internalAuth.getUserId()

        return inboxService.readById(userId, id)
    }
}
