package org.gitanimals.notification.app.event

data class NewPetDropRateDistributionEvent(
    val traceId: Long,
    val type: String,
    val distributions: List<Distribution>,
) {
    data class Distribution(
        val dropRate: Double,
        val count: Int,
    )
}
