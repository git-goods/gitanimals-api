package org.gitanimals.quiz.domain.context

import jakarta.persistence.*
import org.gitanimals.core.IdGenerator
import org.gitanimals.quiz.domain.core.AbstractTime
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Level

@Entity
@Table(name = "quiz_solve_context_quiz")
class QuizSolveContextQuiz(
    @Id
    @Column(name = "id")
    val id: Long,

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_solve_context_id")
    val quizSolveContext: QuizSolveContext,
) : AbstractTime() {

    companion object {
        fun of(
            level: Level,
            category: Category,
            problem: String,
            expectedAnswer: String,
            quizSolveContext: QuizSolveContext,
        ): QuizSolveContextQuiz {
            return QuizSolveContextQuiz(
                id = IdGenerator.generate(),
                level = level,
                category = category,
                problem = problem,
                expectedAnswer = expectedAnswer,
                quizSolveContext = quizSolveContext,
            )
        }
    }
}
