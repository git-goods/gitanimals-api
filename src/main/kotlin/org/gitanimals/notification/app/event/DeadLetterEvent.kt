package org.gitanimals.notification.app.event

data class DeadLetterEvent(
    val traceId: String,
    val deadLetterId: String,
    val sagaId: String,
    val nodeName: String,
    val group: String,
    val deadLetter: String,
)
