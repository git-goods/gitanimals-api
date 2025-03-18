package org.gitanimals.quiz.app

fun interface TextSimilarityChecker {

    fun getSimilarity(text: String): SimilarityResponse
}

data class SimilarityResponse(
    val similarityQuizIds: List<Long>,
)
