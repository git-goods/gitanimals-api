package org.gitanimals.quiz.app.request

import org.gitanimals.quiz.domain.Category
import org.gitanimals.quiz.domain.Level

data class CreateQuizRequest(
    val level: Level,
    val category: Category,
    val problem: String,
    val expectedAnswer: String,
)
