package org.gitanimals.inbox.infra

import org.gitanimals.inbox.domain.InboxService
import org.gitanimals.inbox.infra.event.InboxInputEvent
import org.rooftop.netx.api.SagaStartEvent
import org.rooftop.netx.api.SagaStartListener
import org.rooftop.netx.api.SuccessWith
import org.rooftop.netx.meta.SagaHandler

@SagaHandler
class InboxHandler(
    private val inboxService: InboxService,
) {

    @SagaStartListener(InboxInputEvent::class, successWith = SuccessWith.END)
    fun inboxInputHandler(sagaStartEvent: SagaStartEvent) {
        val inboxInputEvent = sagaStartEvent.decodeEvent(InboxInputEvent::class)

        inboxService.inputInbox(
            userId = inboxInputEvent.inboxData.userId,
            type = inboxInputEvent.inboxData.type,
            title = inboxInputEvent.inboxData.title,
            body = inboxInputEvent.inboxData.body,
            image = inboxInputEvent.inboxData.image,
            redirectTo = inboxInputEvent.inboxData.redirectTo,
            publisher = inboxInputEvent.publisher.publisher,
            publishedAt = inboxInputEvent.publisher.publishedAt,
        )
    }
}
