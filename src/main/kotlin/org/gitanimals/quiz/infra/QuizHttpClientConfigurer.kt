package org.gitanimals.quiz.infra

import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.quiz.app.IdentityApi
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
@Profile("!test")
class QuizHttpClientConfigurer(
    @Value("\${internal.secret}") private val internalSecret: String,
) {

    @Bean
    fun quizIdentityApiHttpClient(): IdentityApi {
        val restClient = RestClient
            .builder()
            .requestInterceptor { request, body, execution ->
                request.headers.add(TRACE_ID, MDC.get(TRACE_ID))
                if (request.uri.path.startsWith("/internals")) {
                    request.headers.add(INTERNAL_SECRET_KEY, internalSecret)
                }
                execution.execute(request, body)
            }
            .defaultStatusHandler(quizHttpClientErrorHandler())
            .baseUrl("https://api.gitanimals.org")
            .build()

        val httpServiceProxyFactory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build()

        return httpServiceProxyFactory.createClient(IdentityApi::class.java)
    }

    @Bean
    fun quizHttpClientErrorHandler(): HttpClientErrorHandler = HttpClientErrorHandler()

    private companion object {
        private const val INTERNAL_SECRET_KEY = "Internal-Secret"
    }
}

@Configuration
@Profile("test")
class QuizTestHttpClientConfigurer {

    @Bean
    fun quizIdentityApiHttpClient(): IdentityApi {
        val restClient = RestClient
            .builder()
            .defaultStatusHandler(HttpClientErrorHandler())
            .baseUrl("http://localhost:8080")
            .build()

        val httpServiceProxyFactory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build()

        return httpServiceProxyFactory.createClient(IdentityApi::class.java)
    }

    @Bean
    fun rankHttpClientErrorHandler(): HttpClientErrorHandler = HttpClientErrorHandler()
}
