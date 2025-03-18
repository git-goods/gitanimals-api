package org.gitanimals.quiz.domain

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotApprovedQuizService(
    private val notApprovedQuizRepository: NotApprovedQuizRepository,
) {

    @Transactional
    fun createNotApprovedQuiz(
        userId: Long,
        problem: String,
        level: Level,
        category: Category,
        expectedAnswer: String,
    ): NotApprovedQuiz {
        return notApprovedQuizRepository.save(
            NotApprovedQuiz.create(
                userId = userId,
                problem = problem,
                level = level,
                category = category,
                expectedAnswer = expectedAnswer,
            )
        )
    }
}
