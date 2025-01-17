package org.gitanimals.gotcha.infra

import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.gotcha.app.RenderApi
import org.gitanimals.gotcha.domain.DropRateClient
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DropRateCache(
    private val renderApi: RenderApi,
) : DropRateClient {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)
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
        val personas = runCatching {
            MDC.put(TRACE_ID, IdGenerator.generate().toString())
            renderApi.getAllPersonas().personas
        }.getOrElse {
            logger.error("Fail to cache drop rate retry after 10 minutes", it)
            emptyList()
        }.also {
            MDC.remove(TRACE_ID)
        }

        personas.forEach { persona ->
            dropRates[persona.type] = persona.dropRate.replace("%", "").toDouble()
        }
    }


    companion object {
        private const val EVERY_10_MINUTES = "0 */10 * * * *"
        private const val DEFAULT_DROP_RATE: Double = 100.0
    }
}
