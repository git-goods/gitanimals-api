package org.gitanimals.notification.app.event

import java.time.ZonedDateTime

data class DailyProductReport(
    val date: ZonedDateTime,
    val totalOnSaleCount: Long,
    val totalSoldOutCount: Long,
    val yesterdayOnSaleCount: Long,
    val yesterdaySoldOutCount: Long,
)
