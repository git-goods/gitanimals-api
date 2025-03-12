package org.gitanimals.quiz.infra.similarity

import org.springframework.web.service.annotation.PostExchange

fun interface Tokenizer {

    @PostExchange("/pipeline/feature-extraction/sentence-transformers/all-mpnet-base-v2")
    fun tokenize(request: Request): List<Double>

    data class Request(
        val inputs: String,
    )
}
