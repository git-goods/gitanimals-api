package org.gitanimals.core

import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler
import java.net.URI


class HttpClientErrorHandler : ResponseErrorHandler {

    override fun hasError(response: ClientHttpResponse): Boolean {
        return response.statusCode.isError
    }

    override fun handleError(url: URI, method: HttpMethod, response: ClientHttpResponse) {
        val body = response.body.bufferedReader().use { it.readText() }
        when {
            response.statusCode.isSameCodeAs(HttpStatus.UNAUTHORIZED) -> run {
                logger.info("[HttpClientErrorHandler] Unauthorization exception \"$body\"")
                throw AuthorizationException(body)
            }

            response.statusCode.is4xxClientError -> run {
                logger.warn("[HttpClientErrorHandler] IllegalArgumentException \"$body\"")
                throw IllegalArgumentException(body)
            }

            else -> run {
                logger.error("[HttpClientErrorHandler] Something went wrong. \"$body\"")
                error(body)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.simpleName)
    }
}
