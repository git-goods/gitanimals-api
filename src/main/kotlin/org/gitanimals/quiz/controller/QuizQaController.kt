package org.gitanimals.quiz.controller

import org.gitanimals.core.instant
import org.gitanimals.quiz.domain.context.QuizSolveContext
import org.gitanimals.quiz.domain.context.QuizSolveContextRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.ZoneId

@RestController
class QuizQaController(
    private val quizSolveContextRepository: QuizSolveContextRepository,
    @Value("\${test.secret}") private val testSecret: String,
) {

    @DeleteMapping("/quizs/qa/context")
    fun deleteQuizContext(
        @RequestParam("user-id") userId: Long,
        @RequestHeader("Test-Secret") testSecret: String
    ) {
        require(testSecret == this.testSecret) { "Not matched testSecret" }

        val now = LocalDate.ofInstant(instant(), ZoneId.of("Asia/Seoul"))
        val quizSolveContext: QuizSolveContext? = quizSolveContextRepository.findQuizSolveContextByUserIdAndSolvedAt(userId, now)

        quizSolveContext?.let {
            quizSolveContextRepository.deleteById(it.id)
        }
    }
}
