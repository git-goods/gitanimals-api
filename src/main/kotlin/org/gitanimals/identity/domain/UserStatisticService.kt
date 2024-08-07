package org.gitanimals.identity.domain

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneOffset

@Service
class UserStatisticService(
    private val userStatisticRepository: UserStatisticRepository,
) {

    fun getYesterdayUserCount(): Int {
        val yesterday = LocalDate.now().minusDays(1)
        val startDay = yesterday.atTime(0, 0, 0).toInstant(ZoneOffset.UTC)
        val endDay = yesterday.atTime(23, 59, 59).toInstant(ZoneOffset.UTC)
        return userStatisticRepository.getDailyUserCount(startDay, endDay)
    }

    @Cacheable(cacheNames = ["total_user_count_cache"], cacheManager = "identityCacheManager")
    fun getTotalUserCount(): Long = userStatisticRepository.count()
}
