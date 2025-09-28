package org.gitanimals.quiz.infra.similarity

import org.gitanimals.quiz.infra.HttpClientErrorHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class TokenizerHttpClientConfigurer(
    @Value("\${openai.key}") private val apiKey: String,
) {

    @Bean
    fun tokenizerApi(): Tokenizer {
        val restClient = RestClient.builder()
            .requestInterceptor { request, body, execution ->
                request.headers.add(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
                execution.execute(request, body)
            }
            .baseUrl("https://api.openai.com")
            .defaultStatusHandler(tokenizerHttpClientErrorHandler())
            .build()

        val httpServiceProxyFactory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build()

        return httpServiceProxyFactory.createClient(Tokenizer::class.java)
    }

    @Bean
    fun tokenizerHttpClientErrorHandler(): HttpClientErrorHandler = HttpClientErrorHandler()
}
