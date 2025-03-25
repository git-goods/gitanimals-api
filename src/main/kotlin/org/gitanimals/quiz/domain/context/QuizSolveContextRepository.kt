package org.gitanimals.quiz.domain.context

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface QuizSolveContextRepository : JpaRepository<QuizSolveContext, Long> {

    fun findQuizSolveContextByUserIdAndSolvedAt(
        userId: Long,
        solvedAt: LocalDate,
    ): QuizSolveContext?
}
