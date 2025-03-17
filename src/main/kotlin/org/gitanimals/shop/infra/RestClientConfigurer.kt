package org.gitanimals.shop.infra

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestClient

@Profile("!prod")
@Configuration("shop.RestClientConfigurer")
class RestClientConfigurer {

    @Bean("shop.renderRestClient")
    fun renderRestClient(): RestClient = RestClient.create("https://render.gitanimals.org")

    @Bean("shop.identityRestClient")
    fun identityRestClient(): RestClient = RestClient.create("https://api.gitanimals.org")
}
