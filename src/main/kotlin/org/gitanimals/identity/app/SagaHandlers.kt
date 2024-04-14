package org.gitanimals.identity.app

import org.gitanimals.identity.app.event.GavePoint
import org.gitanimals.identity.domain.UserService
import org.rooftop.netx.api.SagaCommitEvent
import org.rooftop.netx.api.SagaCommitListener
import org.rooftop.netx.api.SagaStartListener
import org.rooftop.netx.meta.SagaHandler

@SagaHandler
class SagaHandlers(
    private val userService: UserService,
) {

    @SagaCommitListener(GavePoint::class)
    fun givePointHandler(sagaCommitEvent: SagaCommitEvent) {
        val gavePoint = sagaCommitEvent.decodeEvent(GavePoint::class)
        userService.givePoint(gavePoint.username, gavePoint.point)
    }
}
