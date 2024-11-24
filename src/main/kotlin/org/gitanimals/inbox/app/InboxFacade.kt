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

        val inboxApplication = inboxService.findAllUnreadByUserId(userId)
        inboxService.readAllByUserId(userId)

        return inboxApplication
    }
}
