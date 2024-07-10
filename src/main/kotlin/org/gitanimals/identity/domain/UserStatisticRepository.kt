package org.gitanimals.identity.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface UserStatisticRepository : JpaRepository<User, Long> {

    @Query("select count(u.id) from users u where u.created_at between :startDay and :endDay")
    fun getDailyUserCount(
        @Param("startDay") startDay: Instant,
        @Param("endDay") endDay: Instant,
    ): Int
}
