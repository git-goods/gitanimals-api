package org.gitanimals.auction.domain

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneOffset

@Service
@Transactional(readOnly = true)
class ProductStatisticService(
    private val productStatisticRepository: ProductStatisticRepository,
) {

    fun getProductYesterdayStatistic(): Map<ProductState, Long> {
        val current = LocalDate.now()
        val startDay = current.atTime(0, 0, 0).toInstant(ZoneOffset.UTC)
        val endDay = current.atTime(23, 59, 59).toInstant(ZoneOffset.UTC)
        return productStatisticRepository.getDailyCountPerState(startDay, endDay)
            .associate { it.state to it.count }
    }

    fun getProductTotalStatistic(): Map<ProductState, Long> =
        productStatisticRepository.getTotalCountPerState().associate { it.state to it.count }
}
