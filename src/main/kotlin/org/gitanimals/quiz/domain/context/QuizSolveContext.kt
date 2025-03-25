package org.gitanimals.quiz.domain.context

import jakarta.persistence.*
import org.gitanimals.core.AggregateRoot
import org.gitanimals.core.IdGenerator
import org.gitanimals.core.instant
import org.gitanimals.quiz.domain.approved.Quiz
import org.gitanimals.quiz.domain.core.AbstractTime
import org.gitanimals.quiz.domain.core.Category
import java.time.LocalDate
import java.time.ZoneId

@Entity
@Table(
    name = "quiz_solve_context",
    indexes = [
        Index(columnList = "user_id, solved_at")
    ]
)
@AggregateRoot
class QuizSolveContext(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    val category: Category,

    @OneToMany(mappedBy = "quizSolveContext", fetch = FetchType.LAZY)
    val quizSolveContextQuiz: MutableList<QuizSolveContextQuiz>,

    @Embedded
    val solveStage: SolveStage,

    @Column(name = "solved_at")
    val solvedAt: LocalDate,
) : AbstractTime() {

    companion object {

        const val MAX_SOLVE_STAGE = 5

        fun of(
            userId: Long,
            category: Category,
            quizs: List<Quiz>,
        ): QuizSolveContext {
            val quizSolveContext = QuizSolveContext(
                id = IdGenerator.generate(),
                userId = userId,
                category = category,
                quizSolveContextQuiz = mutableListOf(),
                solveStage = SolveStage(
                    maxSolveStage = MAX_SOLVE_STAGE,
                    currentStage = 0,
                    currentStageTimeout = null,
                ),
                solvedAt = LocalDate.ofInstant(instant(), ZoneId.of("Asia/Seoul")),
            )

            quizSolveContext.quizSolveContextQuiz + quizs.map {
                QuizSolveContextQuiz.of(
                    level = it.level,
                    category = it.category,
                    problem = it.problem,
                    expectedAnswer = it.expectedAnswer,
                    quizSolveContext = quizSolveContext,
                )
            }

            return quizSolveContext
        }
    }
}
