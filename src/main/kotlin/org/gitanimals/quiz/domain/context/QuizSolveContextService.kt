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
        val now = LocalDate.ofInstant(instant(), ZoneId.of("UTC"))
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

    @Transactional
    fun getAndStartSolveQuizContext(id: Long, userId: Long): QuizSolveContext {
        val quizSolveContext = getQuizSolveContextByIdAndUserId(id, userId)

        quizSolveContext.startSolve()

        return quizSolveContext
    }

    @Transactional
    fun solveQuiz(id: Long, userId: Long, answer: String) {
        val quizSolveContext = getQuizSolveContextByIdAndUserIdWithLock(id, userId)

        quizSolveContext.solve(answer)
    }

    @Transactional
    fun stopQuizByIdAndUserId(id: Long, userId: Long) {
        val quizSolveContext = getQuizSolveContextByIdAndUserIdWithLock(id, userId)

        quizSolveContext.stopSolve()
    }

    @Transactional(readOnly = true)
    fun findTodaySolvedContext(userId: Long): QuizSolveContext? {
        val now = LocalDate.ofInstant(instant(), ZoneId.of("UTC"))

        return quizSolveContextRepository.findQuizSolveContextByUserIdAndSolvedAt(userId, now)
    }

    private fun getQuizSolveContextByIdAndUserIdWithLock(
        id: Long,
        userId: Long
    ): QuizSolveContext {
        return quizSolveContextRepository.findByIdAndUserIdWithLock(id, userId)
            ?: throw IllegalArgumentException("Cannot find quizContext by id: \"$id\" and userId: \"$userId\"")
    }

    fun getQuizSolveContextByIdAndUserId(
        id: Long,
        userId: Long
    ): QuizSolveContext {
        return quizSolveContextRepository.findByIdAndUserId(id, userId)
            ?: throw IllegalArgumentException("Cannot find quizContext by id: \"$id\" and userId: \"$userId\"")
    }
}
