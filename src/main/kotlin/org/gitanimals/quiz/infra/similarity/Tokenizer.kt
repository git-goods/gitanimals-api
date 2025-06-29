package org.gitanimals.quiz.infra.similarity

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.PostExchange

fun interface Tokenizer {

    @PostExchange("/v1/embeddings")
    fun embed(@RequestBody request: Request): Response

    data class Request(
        val input: String,
        val model: String = "text-embedding-3-small",
        @JsonProperty("encoding_format")
        val format: String = "float",
    ) {

        companion object {
            fun from(input: String): Request {
                return Request(input = input)
            }
        }
    }

    data class Response(
        val usage: Usage,
        val model: String,
        val data: List<Data>,
    ) {
        data class Usage(
            @JsonProperty("prompt_token")
            val promptToken: Int,
            @JsonProperty("total_token")
            val totalToken: Int,
        )

        data class Data(
            val `object`: String,
            val embedding: List<Float>,
        )
    }
}
