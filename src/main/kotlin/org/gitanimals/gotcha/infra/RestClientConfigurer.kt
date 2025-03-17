package org.gitanimals.gotcha.infra

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestClient

@Configuration
@Profile("!prod")
class RestClientConfigurer {

    @Bean("renderRestClient")
    fun renderRestClient(): RestClient = RestClient.create("https://render.gitanimals.org")

    @Bean("userRestClient")
    fun userRestClient(): RestClient = RestClient.create("https://api.gitanimals.org")
}
