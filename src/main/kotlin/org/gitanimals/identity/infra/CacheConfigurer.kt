package org.gitanimals.identity.infra

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

@Configuration("identityCacheConfigurer")
class CacheConfigurer {

    @Primary
    @Bean("identityCacheManager")
    fun cacheManager(): CacheManager {
        val caffeineCacheManager = CaffeineCacheManager()
        caffeineCacheManager.setCaffeine(caffeineConfig())
        return caffeineCacheManager
    }

    @Bean("identityCaffeineConfig")
    fun caffeineConfig(): Caffeine<Any, Any> =
        Caffeine.newBuilder().expireAfterWrite(1.hours.toJavaDuration())
}
