package org.gitanimals.quiz.infra.similarity

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.PostExchange

fun interface Tokenizer {

    @PostExchange("/pipeline/feature-extraction/sentence-transformers/all-mpnet-base-v2")
    fun tokenize(@RequestBody request: Request): List<Float>

    data class Request(
        val inputs: String,
    )
}
