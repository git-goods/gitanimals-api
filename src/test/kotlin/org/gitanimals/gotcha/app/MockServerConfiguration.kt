package org.gitanimals.gotcha.app

import io.mockk.every
import io.mockk.mockk
import org.gitanimals.gotcha.domain.DropRateClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class MockServerConfiguration {

    @Bean
    fun renderApi(): RenderApi = mockk(relaxed = true)

    @Bean
    fun userApi(): UserApi = mockk(relaxed = true)

    @Bean
    fun dropRateClient(): DropRateClient = mockk<DropRateClient>(relaxed = true).apply {
        val client = this
        every { client.getDropRate(any()) } returns 1.0
    }
}
