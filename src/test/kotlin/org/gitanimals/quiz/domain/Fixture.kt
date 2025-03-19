package org.gitanimals.quiz.domain

import org.gitanimals.core.IdGenerator

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
)
