package org.gitanimals.identity.infra

import org.gitanimals.core.HttpClientErrorHandler
import org.gitanimals.identity.app.AppleOauth2Api
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class HttpClientConfigurer {

    @Bean
    fun appleOauth2Api(): AppleOauth2Api {
        val restClient = RestClient
            .builder()
            .defaultStatusHandler(HttpClientErrorHandler())
            .baseUrl("https://appleid.apple.com")
            .build()

        val httpServiceProxyFactory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build()

        return httpServiceProxyFactory.createClient(AppleOauth2Api::class.java)
    }
}
