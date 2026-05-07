package org.gitanimals.core.event

import com.mixpanel.mixpanelapi.ClientDelivery
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import org.gitanimals.core.GracefulShutdownDispatcher.gracefulLaunch
import org.json.JSONObject
import org.slf4j.LoggerFactory

class MixpanelEventLogger(
    private val mixpanelAPI: MixpanelAPI,
    private val messageBuilder: MessageBuilder,
) : EventLogger {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun track(eventName: String, distinctId: String, properties: Map<String, Any?>) {
        gracefulLaunch {
            runCatching {
                val props = JSONObject()
                properties.forEach { (k, v) -> if (v != null) props.put(k, v) }
                val message = messageBuilder.event(distinctId, eventName, props)
                val delivery = ClientDelivery()
                delivery.addMessage(message)
                mixpanelAPI.deliver(delivery)
            }.onFailure {
                logger.warn("Failed to track Mixpanel event '{}': {}", eventName, it.message)
            }
        }
    }
}
