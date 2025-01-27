package org.gitanimals.identity.saga

import org.gitanimals.identity.app.event.GavePoint
import org.gitanimals.identity.domain.UserService
import org.rooftop.netx.api.SagaCommitEvent
import org.rooftop.netx.api.SagaCommitListener
import org.rooftop.netx.meta.SagaHandler
import org.slf4j.LoggerFactory

@SagaHandler
class GivePointHandlers(
    private val userService: UserService,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    @SagaCommitListener(GavePoint::class)
    fun givePointHandler(sagaCommitEvent: SagaCommitEvent) {
        val gavePoint = sagaCommitEvent.decodeEvent(GavePoint::class)
        runCatching {
            userService.givePoint(gavePoint.username, gavePoint.point, "FOR_COMMIT")
        }.onFailure {
            logger.error("Cannot give point to user. username: \"${gavePoint.username}\", point: \"${gavePoint.point}\"", it)
        }
    }
}
