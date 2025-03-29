package org.gitanimals.quiz.infra.similarity

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface QuizSimilarityRepository : ElasticsearchRepository<QuizSimilarity, Long> {

    fun deleteByQuizId(quizId: Long)
}
