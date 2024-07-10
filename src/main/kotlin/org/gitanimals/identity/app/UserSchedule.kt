package org.gitanimals.identity.app

import org.gitanimals.identity.app.event.UserYesterdayReport
import org.gitanimals.identity.core.instant
import org.gitanimals.identity.core.toKr
import org.gitanimals.identity.domain.UserStatisticService
import org.rooftop.netx.api.SagaManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class UserSchedule(
    private val sagaManager: SagaManager,
    private val userStatisticService: UserStatisticService,
) {

    @Scheduled(cron = EVERY_9AM)
    fun sendYesterdayNewUserReport() {
        val yesterday = instant().toKr().minusDays(1)
        val yesterdayUserCount = userStatisticService.getYesterdayUserCount()
        val totalUserCount = userStatisticService.getTotalUserCount()

        val userYesterdayReport = UserYesterdayReport(
            date = yesterday,
            yesterdayNewUserCount = yesterdayUserCount,
            totalUserCount = totalUserCount,
            serverName = SERVER_NAME,
        )

        sagaManager.startSync(userYesterdayReport)
    }

    private companion object {
        private const val EVERY_9AM = "0 0 9 * * ?"
        private const val SERVER_NAME = "IDENTITY"
    }
}
