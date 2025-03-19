package org.gitanimals.quiz.app.request

import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Level

data class CreateQuizRequest(
    val level: Level,
    val category: Category,
    val problem: String,
    val expectedAnswer: String,
)
