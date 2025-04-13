package org.gitanimals.quiz.controller

import org.gitanimals.quiz.domain.context.QuizSolveContextRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class QuizQaController(
    private val quizSolveContextRepository: QuizSolveContextRepository,
    @Value("\${test.secret}") private val testSecret: String,
) {

    @DeleteMapping("/quizs/qa/context")
    fun deleteQuizContext(
        @RequestHeader("Test-Secret") testSecret: String
    ) {
        require(testSecret == this.testSecret) { "Not matched testSecret" }

        quizSolveContextRepository.deleteAll()
    }
}
