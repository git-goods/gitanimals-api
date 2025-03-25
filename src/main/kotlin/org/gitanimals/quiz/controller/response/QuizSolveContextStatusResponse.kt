package org.gitanimals.quiz.controller.response

import org.gitanimals.quiz.domain.context.QuizSolveContextStatus

data class QuizSolveContextStatusResponse(
    val result: QuizSolveContextStatus,
)
