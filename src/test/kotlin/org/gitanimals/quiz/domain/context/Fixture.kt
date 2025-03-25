package org.gitanimals.quiz.domain.context

import org.gitanimals.quiz.domain.approved.Quiz
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.quiz.quiz

fun quizSolveContext(
    userId: Long = 0L,
    category: Category = Category.BACKEND,
    quizs: List<Quiz> = listOf(
        quiz(problem = "1"),
        quiz(problem = "2")
    )
): QuizSolveContext = QuizSolveContext.of(
    userId = userId,
    category = category,
    quizs = quizs
)
