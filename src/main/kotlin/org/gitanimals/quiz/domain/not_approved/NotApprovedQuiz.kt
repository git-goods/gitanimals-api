package org.gitanimals.quiz.domain.not_approved

import jakarta.persistence.*
import org.gitanimals.core.AggregateRoot
import org.gitanimals.core.IdGenerator
import org.gitanimals.quiz.domain.approved.Quiz
import org.gitanimals.quiz.domain.core.AbstractTime
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language
import org.gitanimals.quiz.domain.core.Language.Companion.containsKorean
import org.gitanimals.quiz.domain.core.Level

@Entity
@AggregateRoot
@Table(name = "not_approval_quiz")
class NotApprovedQuiz(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id", nullable = false, unique = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "level", columnDefinition = "VARCHAR(50)", nullable = false)
    val level: Level,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", columnDefinition = "TEXT", nullable = false)
    val category: Category,

    @Column(name = "problem", length = 1000, columnDefinition = "VARCHAR(1000)", nullable = false)
    val problem: String,

    @Column(name = "expected_answer", columnDefinition = "TEXT", nullable = false)
    val expectedAnswer: String,

    @Column(name = "language", columnDefinition = "TEXT DEFAULT 'KOREA'", nullable = true)
    val language: Language,
) : AbstractTime() {

    fun toQuiz(): Quiz {
        return Quiz.create(
            userId = userId,
            level = level,
            problem = problem,
            category = category,
            expectedAnswer = expectedAnswer,
        )
    }

    companion object {

        fun create(
            userId: Long,
            level: Level,
            problem: String,
            category: Category,
            expectedAnswer: String,
        ): NotApprovedQuiz {
            return NotApprovedQuiz(
                id = IdGenerator.generate(),
                userId = userId,
                level = level,
                problem = problem,
                category = category,
                expectedAnswer = expectedAnswer,
                language = when {
                    problem.containsKorean() -> Language.KOREA
                    else -> Language.ENGLISH
                }
            )
        }
    }
}
