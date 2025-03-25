package org.gitanimals.quiz.app.request

import org.gitanimals.quiz.domain.core.Category

data class CreateSolveQuizRequest(
    val category: Category,
)
