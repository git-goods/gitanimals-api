package org.gitanimals.quiz.domain

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QuizService(
    private val quizRepository: QuizRepository,
) {

    @Transactional
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
}
