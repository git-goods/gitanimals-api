package org.gitanimals.quiz.domain.context

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface QuizSolveContextRepository : JpaRepository<QuizSolveContext, Long> {

    fun findQuizSolveContextByUserIdAndSolvedAt(
        userId: Long,
        solvedAt: LocalDate,
    ): QuizSolveContext?

    @Query("select q from QuizSolveContext q join fetch q.quizSolveContextQuiz where q.id = :id and q.userId = :userId")
    fun findByIdAndUserId(
        id: Long,
        userId: Long,
    ): QuizSolveContext?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select q from QuizSolveContext q join fetch q.quizSolveContextQuiz where q.id = :id and q.userId = :userId")
    fun findByIdAndUserIdWithLock(
        id: Long,
        userId: Long,
    ): QuizSolveContext?

    fun deleteByUserId(userId: Long)
}
