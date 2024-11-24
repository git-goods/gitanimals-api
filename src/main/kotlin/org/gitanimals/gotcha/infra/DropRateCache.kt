package org.gitanimals.gotcha.infra

import org.gitanimals.gotcha.app.RenderApi
import org.gitanimals.gotcha.domain.DropRateClient
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DropRateCache(
    private val renderApi: RenderApi,
) : DropRateClient {

    private var dropRates = mutableMapOf<String, Double>()

    override fun getDropRate(name: String): Double = dropRates[name] ?: DEFAULT_DROP_RATE

    @Scheduled(cron = EVERY_10_MINUTES)
    fun updateDropRateBatch() {
        updateDropRate()
    }

    @EventListener(ApplicationStartedEvent::class)
    fun initDropRate() {
        updateDropRate()
    }

    fun updateDropRate() {
        val personas = renderApi.getAllPersonas().personas

        personas.forEach { persona ->
            dropRates[persona.type] = persona.dropRate.replace("%", "").toDouble()
        }
    }


    companion object {
        private const val EVERY_10_MINUTES = "0 */10 * * * *"
        private const val DEFAULT_DROP_RATE: Double = 100.0
    }
}
