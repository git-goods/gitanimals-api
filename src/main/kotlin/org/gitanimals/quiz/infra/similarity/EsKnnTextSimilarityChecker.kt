package org.gitanimals.quiz.infra.similarity

import org.gitanimals.quiz.app.SimilarityResponse
import org.gitanimals.quiz.app.TextSimilarityChecker
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Component

@Component
class EsKnnTextSimilarityChecker(
    private val elasticSearchOperations: ElasticsearchOperations,
    private val tokenizer: Tokenizer,
) : TextSimilarityChecker {

    override fun getSimilarity(text: String): SimilarityResponse {
        val tokenizedText = tokenizer.tokenize(Tokenizer.Request(text))

        val knnQuery = NativeQuery.builder()
            .withKnnSearches {
                it.field(QuizSimilarity::vector.name)
                it.queryVector(tokenizedText)
                it.similarity(0.75F)
                it.k(MAX_RETURN_KNN_SIZE)
                it.numCandidates(MAX_RETURN_KNN_SIZE * 5)
            }.build()

        val searchHits = elasticSearchOperations.search(knnQuery, QuizSimilarity::class.java)

        return SimilarityResponse(
            searchHits.searchHits.map { it.content.quizId }
        )
    }

    companion object {
        private const val MAX_RETURN_KNN_SIZE = 5
    }
}
