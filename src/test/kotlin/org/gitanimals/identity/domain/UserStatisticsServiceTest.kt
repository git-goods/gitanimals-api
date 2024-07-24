package org.gitanimals.identity.domain

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import org.gitanimals.identity.infra.CacheConfigurer
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@EnableCaching
@ContextConfiguration(
    classes = [
        CacheConfigurer::class,
        UserStatisticService::class,
    ]
)
@TestPropertySource("classpath:test.properties")
@DisplayName("UserStatisticsService 클래스의")
@EntityScan(basePackages = ["org.gitanimals.identity.domain"])
@EnableJpaRepositories(basePackages = ["org.gitanimals.identity.domain"])
class UserStatisticsServiceTest(
    private val cacheManager: CacheManager,
    private val userStatisticService: UserStatisticService,
) : DescribeSpec({

    describe("getTotalUserCount 메소드는") {
        context("호출되면,") {
            it("total_user_count 이름의 캐시를 저장한다.") {
                val totalUserCount = userStatisticService.getTotalUserCount()

                totalUserCount shouldNotBe null
                cacheManager.getCache("total_user_count_cache")!![SimpleKey.EMPTY] shouldNotBe null
            }
        }
    }
})
