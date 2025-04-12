package org.gitanimals.quiz.app.response

import org.gitanimals.quiz.domain.context.QuizSolveContext
import org.gitanimals.quiz.domain.context.QuizSolveContextStatus

data class TodaySolvedContextResponse(
    val isSolved: Boolean,
    val contextId: String?,
    val prize: Int?,
    val result: QuizSolveContextStatus?,
) {

    companion object {

        fun from(quizSolveContext: QuizSolveContext?): TodaySolvedContextResponse {
            if (quizSolveContext == null) {
                return TodaySolvedContextResponse(
                    isSolved = false,
                    contextId = null,
                    prize = null,
                    result = null,
                )
            }

            return TodaySolvedContextResponse(
                isSolved = true,
                contextId = quizSolveContext.id.toString(),
                prize = quizSolveContext.getPrize(),
                result = quizSolveContext.getStatus(),
            )
        }
    }
}
