package org.gitanimals.notification.app.event

import java.time.ZonedDateTime

data class UserYesterdayReport(
    val date: ZonedDateTime,
    val yesterdayNewUserCount: Int,
    val totalUserCount: Long,
    val serverName: String,
)
