package org.gitanimals.quiz.domain.approved

import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Level
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val quizRepository: QuizRepository,
) {

    fun createNewQuiz(
        userId: Long,
        problem: String,
        level: Level,
        category: Category,
        expectedAnswer: String,
    ): Quiz {
        return quizRepository.save(
            Quiz.create(
                userId = userId,
                problem = problem,
                level = level,
                category = category,
                expectedAnswer = expectedAnswer,
            )
        )
    }

    fun findAllByIds(similarityQuizIds: List<Long>): List<Quiz> =
        quizRepository.findAllById(similarityQuizIds)
}
