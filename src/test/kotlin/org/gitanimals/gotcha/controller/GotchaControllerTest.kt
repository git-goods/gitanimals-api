package org.gitanimals.gotcha.controller

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gitanimals.core.event.EventLogger
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ID
import org.gitanimals.gotcha.app.GotchaFacadeV3
import org.gitanimals.gotcha.app.response.GotchaResponseV3
import org.gitanimals.gotcha.domain.GotchaType
import org.slf4j.MDC

internal class GotchaControllerTest : DescribeSpec({

    val gotchaFacadeV3 = mockk<GotchaFacadeV3>()
    val eventLogger = mockk<EventLogger>(relaxed = true)
    val controller = GotchaController(gotchaFacadeV3, eventLogger)

    beforeEach {
        MDC.put(USER_ID, "42")
    }

    afterEach {
        MDC.clear()
    }

    describe("gotchaV3 메소드는") {
        context("gotcha가 성공하면") {
            every { gotchaFacadeV3.gotcha(any(), any(), any()) } returns listOf(
                GotchaResponseV3(name = "GOOSE", dropRate = "1.0"),
            )

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
    }
})
