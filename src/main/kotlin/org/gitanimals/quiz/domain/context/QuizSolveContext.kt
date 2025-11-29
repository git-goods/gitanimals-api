package org.gitanimals.quiz.domain.context

import jakarta.persistence.*
import org.gitanimals.core.AggregateRoot
import org.gitanimals.core.IdGenerator
import org.gitanimals.core.instant
import org.gitanimals.quiz.domain.approved.Quiz
import org.gitanimals.quiz.domain.context.QuizSolveContextStatus.Companion.solveTransferableStatus
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

    @OneToMany(mappedBy = "quizSolveContext", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val quizSolveContextQuiz: MutableList<QuizSolveContextQuiz>,

    @Embedded
    val solveStage: SolveStage,

    @Column(name = "prize")
    private var prize: Int,

    @Column(name = "solved_at")
    val solvedAt: LocalDate,

    @Column(name = "status")
    private var status: QuizSolveContextStatus,
) : AbstractTime() {

    fun getStatus() = status

    fun getPrize() = prize

    fun startSolve() {
        require(status in solveTransferableStatus) {
            "Cannot start solve cause quiz is not in progress status : \"$solveTransferableStatus\""
        }

        this.status = QuizSolveContextStatus.SOLVING
        this.solveStage.setNextStage()
    }

    fun solve(answer: String) {
        require(status == QuizSolveContextStatus.SOLVING) {
            "Cannot solve quiz cause is not \"SOLVING\" status"
        }

        val answerTime = instant()

        if (answerTime > solveStage.getCurrentStageTimeout()) {
            this.status = QuizSolveContextStatus.FAIL
            return
        }

        val currentQuiz = getCurrentQuiz()
        if (currentQuiz.isCorrect(answer).not()) {
            this.status = QuizSolveContextStatus.FAIL
            return
        }

        this.status = QuizSolveContextStatus.SUCCESS
        if (solveStage.isLastStage()) {
            this.status = QuizSolveContextStatus.DONE
        }

        if (prize == 0) {
            prize = 2000
        } else {
            prize *= 2
        }
    }

    fun getCurrentQuiz(): QuizSolveContextQuiz {
        val currentStage = solveStage.getCurrentStage()
        return quizSolveContextQuiz[currentStage - 1]
    }

    fun stopSolve() {
        require(this.status == QuizSolveContextStatus.SUCCESS) {
            "Stop quiz is only available in \"SUCCESS\" status."
        }

        this.status = QuizSolveContextStatus.DONE
    }

    companion object {

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
                    maxSolveStage = quizs.size,
                    currentStage = 0,
                    currentStageTimeout = null,
                ),
                prize = 0,
                solvedAt = LocalDate.ofInstant(instant(), ZoneId.of("Asia/Seoul")),
                status = QuizSolveContextStatus.NOT_STARTED,
            )

            quizSolveContext.quizSolveContextQuiz.addAll(quizs.map {
                QuizSolveContextQuiz.of(
                    level = it.level,
                    category = it.category,
                    problem = it.problem,
                    expectedAnswer = it.expectedAnswer,
                    quizSolveContext = quizSolveContext,
                )
            })

            return quizSolveContext
        }
    }
}
