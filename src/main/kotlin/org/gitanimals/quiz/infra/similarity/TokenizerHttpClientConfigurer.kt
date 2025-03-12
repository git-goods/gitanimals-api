package org.gitanimals.quiz.infra.similarity

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class TokenizerHttpClientConfigurer(
    @Value("\${tokenizer.api.key}") private val apiKey: String,
) {

    @Bean
    fun tokenizerApi(errorHandler: ResponseErrorHandler): Tokenizer {
        val restClient = RestClient.builder()
            .requestInterceptor { request, body, execution ->
                request.headers.add(HttpHeaders.AUTHORIZATION, apiKey)
                execution.execute(request, body)
            }
            .baseUrl("https://api-inference.huggingface.co")
            .defaultStatusHandler(errorHandler)
            .build()

        val httpServiceProxyFactory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build()

        return httpServiceProxyFactory.createClient(Tokenizer::class.java)
    }
}
