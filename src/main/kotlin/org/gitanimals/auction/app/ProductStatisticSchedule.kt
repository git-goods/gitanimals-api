package org.gitanimals.auction.app

import org.gitanimals.auction.app.event.DailyProductReport
import org.gitanimals.auction.core.instant
import org.gitanimals.auction.core.toKr
import org.gitanimals.auction.domain.ProductState
import org.gitanimals.auction.domain.ProductStatisticService

import org.rooftop.netx.api.SagaManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ProductStatisticSchedule(
    private val sagaManager: SagaManager,
    private val productStatisticService: ProductStatisticService,
) {

    @Scheduled(cron = EVERY_9AM)
    fun sendDailyProductReport() {
        val yesterday = instant().toKr().minusDays(1)
        val totalReport = productStatisticService.getProductTotalStatistic()
        val yesterdayReport = productStatisticService.getProductYesterdayStatistic()

        sagaManager.startSync(
            DailyProductReport(
                date = yesterday,
                totalOnSaleCount = totalReport.getOrDefault(ProductState.ON_SALE, 0),
                totalSoldOutCount = totalReport.getOrDefault(ProductState.SOLD_OUT, 0),
                yesterdayOnSaleCount = yesterdayReport.getOrDefault(ProductState.ON_SALE, 0),
                yesterdaySoldOutCount = yesterdayReport.getOrDefault(ProductState.SOLD_OUT, 0),
            )
        )
    }

    private companion object {
        private const val EVERY_9AM = "0 0 9 * * ?"
    }
}
