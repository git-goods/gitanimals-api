package org.gitanimals.core.event

import org.slf4j.LoggerFactory

class NoOpEventLogger : EventLogger {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun track(eventName: String, distinctId: String, properties: Map<String, Any?>) {
        logger.debug("NoOpEventLogger.track: event={}, distinctId={}", eventName, distinctId)
    }
}
