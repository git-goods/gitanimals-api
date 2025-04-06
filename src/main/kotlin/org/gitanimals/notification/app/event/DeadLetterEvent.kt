package org.gitanimals.notification.app.event

import org.rooftop.netx.api.SagaEvent

data class DeadLetterEvent(
    val traceId: String,
    val deadLetterId: String,
    val sagaEvent: SagaEvent,
)
