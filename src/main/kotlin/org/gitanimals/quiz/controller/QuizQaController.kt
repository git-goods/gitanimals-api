package org.gitanimals.quiz.controller

import org.gitanimals.quiz.domain.context.QuizSolveContextRepository
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class QuizQaController(
    private val quizSolveContextRepository: QuizSolveContextRepository,
) {

    @DeleteMapping("/quizs/qa/context")
    fun deleteQuizContext(
        @RequestParam("userId") userId: Long,
    ) = quizSolveContextRepository.deleteByUserId(userId)
}
