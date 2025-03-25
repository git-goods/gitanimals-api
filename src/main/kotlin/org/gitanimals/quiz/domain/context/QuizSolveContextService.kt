package org.gitanimals.quiz.domain.context

import org.gitanimals.core.instant
import org.gitanimals.quiz.domain.approved.Quiz
import org.gitanimals.quiz.domain.core.Category
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneId

@Service
class QuizSolveContextService(
    private val quizSolveContextRepository: QuizSolveContextRepository,
) {

    @Transactional
    fun createQuizSolveContext(
        userId: Long,
        category: Category,
        quizs: List<Quiz>,
    ): QuizSolveContext {
        val now = LocalDate.ofInstant(instant(), ZoneId.of("Asia/Seoul"))
        quizSolveContextRepository.findQuizSolveContextByUserIdAndSolvedAt(userId, now)?.let {
            throw IllegalArgumentException("Already solve daily quiz.")
        }

        val quizSolveContext = QuizSolveContext.of(
            userId = userId,
            category = category,
            quizs = quizs,
        )

        return quizSolveContextRepository.save(quizSolveContext)
    }
}
