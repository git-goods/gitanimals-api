package org.gitanimals.gotcha.controller

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gitanimals.core.auth.InternalAuth
import org.gitanimals.core.event.EventLogger
import org.gitanimals.gotcha.app.GotchaFacadeV3
import org.gitanimals.gotcha.app.response.GotchaResponseV3

internal class GotchaControllerTest : DescribeSpec({

    val gotchaFacadeV3 = mockk<GotchaFacadeV3>()
    val eventLogger = mockk<EventLogger>(relaxed = true)
    val internalAuth = mockk<InternalAuth>()
    val controller = GotchaController(gotchaFacadeV3, eventLogger, internalAuth)

    describe("gotchaV3 메소드는") {
        context("gotcha가 성공하면") {
            every { gotchaFacadeV3.gotcha(any(), any(), any()) } returns listOf(
                GotchaResponseV3(name = "GOOSE", dropRate = "1.0"),
            )
            every { internalAuth.findUserId() } returns 42L

            it("complete_gotcha 이벤트를 user_id와 함께 트래킹한다") {
                controller.gotchaV3("token", "DEFAULT", 1)

                verify {
                    eventLogger.track(
                        eventName = "complete_gotcha",
                        distinctId = "42",
                        properties = match { it["user_id"] != null },
                    )
                }
            }
        }

        context("InternalAuth.findUserId()가 null을 반환하면") {
            every { gotchaFacadeV3.gotcha(any(), any(), any()) } returns listOf(
                GotchaResponseV3(name = "GOOSE", dropRate = "1.0"),
            )
            every { internalAuth.findUserId() } returns null

            it("이벤트를 트래킹하지 않고 silent하게 동작한다") {
                controller.gotchaV3("token", "DEFAULT", 1)

                verify(exactly = 0) {
                    eventLogger.track(any(), any(), any())
                }
            }
        }
    }
})
