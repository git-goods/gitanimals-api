package org.gitanimals.quiz.app.response

import com.fasterxml.jackson.annotation.JsonFormat
import org.gitanimals.quiz.domain.context.QuizSolveContext
import org.gitanimals.quiz.domain.context.QuizSolveContextStatus
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Level
import java.time.LocalDateTime
import java.time.ZoneId

data class QuizContextResponse(
    val round: Round,
    val level: Level,
    val category: Category,
    val problem: String,
    val prize: Int,
    val status: QuizSolveContextStatus,
) {

    data class Round(
        val total: Int,
        val current: Int,
        @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "UTC",
        )
        val timeoutAt: LocalDateTime,
    )

    companion object {
        fun from(quizSolveContext: QuizSolveContext): QuizContextResponse {
            val round = Round(
                total = quizSolveContext.solveStage.maxSolveStage,
                current = quizSolveContext.solveStage.getCurrentStage(),
                timeoutAt = LocalDateTime.ofInstant(
                    quizSolveContext.solveStage.getCurrentStageTimeout(),
                    ZoneId.of("UTC")
                ),
            )

            val currentQuiz = quizSolveContext.getCurrentQuiz()

            return QuizContextResponse(
                round = round,
                level = currentQuiz.level,
                category = currentQuiz.category,
                problem = currentQuiz.problem,
                prize = quizSolveContext.prize,
                status = quizSolveContext.getStatus(),
            )
        }
    }
}
