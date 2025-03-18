package org.gitanimals.auction.infra

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestClient

@Profile("!test")
@Configuration("auction.RestClientConfigurer")
class RestClientConfigurer {

    @Bean("auction.renderRestClient")
    fun renderRestClient(): RestClient = RestClient.create("https://render.gitanimals.org")

    @Bean("auction.identityRestClient")
    fun identityRestClient(): RestClient = RestClient.create("https://api.gitanimals.org")
}
