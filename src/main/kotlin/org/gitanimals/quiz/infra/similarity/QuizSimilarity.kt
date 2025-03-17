package org.gitanimals.quiz.infra.similarity

import org.gitanimals.core.IdGenerator
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

private const val BERT_LARGE_DIMS = 768

@Document(indexName = "quiz_similarity", createIndex = true)
class QuizSimilarity(
    @Id
    val id: Long,

    @Field(name = "quiz_id")
    val quizId: Long,

    @Field(name = "vector", type = FieldType.Dense_Vector, dims = BERT_LARGE_DIMS)
    val vector: List<Float>,
) {

    companion object {
        fun from(quizId: Long, vector: List<Float>): QuizSimilarity {
            return QuizSimilarity(
                id = IdGenerator.generate(),
                quizId = quizId,
                vector = vector,
            )
        }
    }
}
