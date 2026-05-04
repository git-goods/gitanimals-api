package org.gitanimals.core.event

interface EventLogger {
    fun track(eventName: String, distinctId: String, properties: Map<String, Any?> = emptyMap())
}
