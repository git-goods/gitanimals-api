package org.gitanimals.supports.deadletter

import org.rooftop.netx.api.SagaEvent
import org.rooftop.netx.spi.DeadLetterListener
import org.springframework.context.ApplicationEventPublisher

class DeadLetterEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : DeadLetterListener {

    override fun listen(deadLetterId: String, sagaEvent: SagaEvent) {
        applicationEventPublisher.publishEvent(
            DeadLetterEvent(
                deadLetterId = deadLetterId,
                sagaEvent = sagaEvent,
            )
        )
    }
}

