package org.gitanimals.core.event

import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class MixpanelConfiguration(
    @Value("\${mixpanel.project.token:}") private val token: String,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    @Profile("!test")
    fun mixpanelEventLogger(): EventLogger {
        if (token.isBlank()) {
            logger.warn("MIXPANEL_PROJECT_TOKEN is blank — falling back to NoOpEventLogger")
            return NoOpEventLogger()
        }
        return MixpanelEventLogger(MixpanelAPI(), MessageBuilder(token))
    }

    @Bean
    @Profile("test")
    fun noOpEventLogger(): EventLogger = NoOpEventLogger()
}
