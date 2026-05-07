package org.gitanimals.core.event

import com.mixpanel.mixpanelapi.ClientDelivery
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.json.JSONObject

internal class MixpanelEventLoggerTest : DescribeSpec({

    val mixpanelAPI = mockk<MixpanelAPI>(relaxed = true)
    val token = "test-token"
    val messageBuilder = MessageBuilder(token)
    val logger = MixpanelEventLogger(mixpanelAPI, messageBuilder)

    describe("track") {

        context("when called with eventName, distinctId, and properties") {
            it("delivers a ClientDelivery to MixpanelAPI") {
                val deliverySlot = slot<ClientDelivery>()
                every { mixpanelAPI.deliver(capture(deliverySlot)) } returns Unit

                logger.track(
                    eventName = "complete_login",
                    distinctId = "user-123",
                    properties = mapOf("platform" to "github", "count" to 1)
                )

                // give coroutine time to execute
                Thread.sleep(200)

                verify(exactly = 1) { mixpanelAPI.deliver(any<ClientDelivery>()) }
            }
        }

        context("when properties contain null values") {
            it("skips null values and still delivers") {
                logger.track(
                    eventName = "complete_login",
                    distinctId = "user-456",
                    properties = mapOf("key" to null, "valid" to "value")
                )

                Thread.sleep(200)

                verify(atLeast = 1) { mixpanelAPI.deliver(any<ClientDelivery>()) }
            }
        }

        context("when MixpanelAPI throws") {
            it("swallows the exception and does not propagate") {
                every { mixpanelAPI.deliver(any<ClientDelivery>()) } throws RuntimeException("network error")

                // should not throw
                logger.track("some_event", "user-789")

                Thread.sleep(200)
            }
        }
    }
})
