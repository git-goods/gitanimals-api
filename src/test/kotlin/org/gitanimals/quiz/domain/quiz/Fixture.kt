package org.gitanimals.quiz.domain.quiz

import org.gitanimals.core.IdGenerator
import org.gitanimals.quiz.domain.approved.Quiz
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language
import org.gitanimals.quiz.domain.core.Level
import org.gitanimals.quiz.domain.not_approved.NotApprovedQuiz

fun quiz(
    id: Long = IdGenerator.generate(),
    userId: Long = IdGenerator.generate(),
    level: Level = Level.MEDIUM,
    category: Category = Category.BACKEND,
    problem: String = "Test problem",
    expectedAnswer: String = "YES",
): Quiz = Quiz(
    id = id,
    userId = userId,
    level = level,
    category = category,
    problem = problem,
    expectedAnswer = expectedAnswer,
    language = Language.KOREA,
)

fun notApprovedQuiz(
    id: Long = IdGenerator.generate(),
    userId: Long = IdGenerator.generate(),
    level: Level = Level.MEDIUM,
    category: Category = Category.BACKEND,
    problem: String = "Test problem",
    expectedAnswer: String = "YES",
): NotApprovedQuiz = NotApprovedQuiz(
    id = id,
    userId = userId,
    level = level,
    category = category,
    problem = problem,
    expectedAnswer = expectedAnswer,
    language = Language.KOREA,
)
