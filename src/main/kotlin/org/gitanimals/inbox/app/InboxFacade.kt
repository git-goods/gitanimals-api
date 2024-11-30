package org.gitanimals.inbox.app

import org.gitanimals.inbox.domain.InboxApplication
import org.gitanimals.inbox.domain.InboxService
import org.springframework.stereotype.Component

@Component
class InboxFacade(
    private val identityApi: IdentityApi,
    private val inboxService: InboxService,
) {

    fun findAllUnreadByToken(token: String): InboxApplication {
        val userId = identityApi.getUserByToken(token).id.toLong()

        return inboxService.findAllByUserId(userId)
    }

    fun readInboxByTokenAndId(token: String, id: Long) {
        val userId = identityApi.getUserByToken(token).id.toLong()

        return inboxService.readById(userId, id)
    }
}
