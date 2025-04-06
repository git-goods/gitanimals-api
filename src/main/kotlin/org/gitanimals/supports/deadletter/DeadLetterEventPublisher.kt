package org.gitanimals.supports.deadletter

import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.rooftop.netx.api.SagaEvent
import org.rooftop.netx.spi.DeadLetterListener
import org.slf4j.MDC
import org.springframework.context.ApplicationEventPublisher

class DeadLetterEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : DeadLetterListener {

    override fun listen(deadLetterId: String, sagaEvent: SagaEvent) {
        runCatching {
            MDC.put(TRACE_ID, IdGenerator.generate().toString())
            applicationEventPublisher.publishEvent(
                DeadLetterEvent(
                    deadLetterId = deadLetterId,
                    sagaId = sagaEvent.id,
                    group = sagaEvent.group,
                    nodeName = sagaEvent.nodeName,
                    deadLetter = sagaEvent.decodeEvent(String::class),
                )
            )
        }.also {
            MDC.remove(TRACE_ID)
        }
    }
}

