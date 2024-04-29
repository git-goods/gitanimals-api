package org.gitanimals.auction.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.context.TestComponent
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestClient

@TestComponent
class MockUserServer(
    objectMapper: ObjectMapper
) : MockServer(objectMapper) {

    @Bean("auction.identityRestClient")
    fun identityRestClient(): RestClient = RestClient.create(mockWebSerer.url("").toString())
}
