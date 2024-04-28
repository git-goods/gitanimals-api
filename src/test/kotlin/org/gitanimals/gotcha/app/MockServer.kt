package org.gitanimals.gotcha.app

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.http.HttpHeaders

abstract class MockServer(
    private val objectMapper: ObjectMapper
) {

    protected val mockWebSerer: MockWebServer = MockWebServer()

    init {
        mockWebSerer.start()
    }

    fun enqueue200() {
        mockWebSerer.enqueue(MockResponse().setResponseCode(200))
    }

    fun enqueue200(vararg response: Any) {
        response.forEach {
            mockWebSerer.enqueue(
                MockResponse().setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .setBody(objectMapper.writeValueAsString(it))
            )
        }
    }

    fun enqueue500() {
        mockWebSerer.enqueue(
            MockResponse().setResponseCode(500)
        )
    }

    fun enqueue400() {
        mockWebSerer.enqueue(
            MockResponse().setResponseCode(400)
        )
    }
}
