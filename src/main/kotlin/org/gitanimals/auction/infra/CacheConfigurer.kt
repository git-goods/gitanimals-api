package org.gitanimals.auction.infra

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

@Configuration("auctionCacheConfigurer")
class CacheConfigurer {

    @Bean("auctionCacheManager")
    fun cacheManager(): CacheManager {
        val caffeineCacheManager = CaffeineCacheManager()
        caffeineCacheManager.setCaffeine(caffeineConfig())
        return caffeineCacheManager
    }

    @Bean("auctionCaffeineConfig")
    fun caffeineConfig(): Caffeine<Any, Any> =
        Caffeine.newBuilder().expireAfterWrite(1.hours.toJavaDuration())
}
