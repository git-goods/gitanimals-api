package org.gitanimals.quiz.controller.admin.response

import com.fasterxml.jackson.annotation.JsonFormat
import org.gitanimals.quiz.domain.approved.Quiz
import org.gitanimals.quiz.domain.context.QuizSolveContext
import org.gitanimals.quiz.domain.context.QuizSolveContextStatus
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language
import org.gitanimals.quiz.domain.core.Level
import org.gitanimals.quiz.domain.not_approved.NotApprovedQuiz
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

data class AdminQuizsResponse(
    val quizs: List<AdminQuizResponse>,
    val nextId: String?,
) {

    companion object {
        fun fromApprovedQuizs(
            quizs: List<Quiz>,
            size: Int,
        ): AdminQuizsResponse {
            val pageQuizs = quizs.take(size)

            return AdminQuizsResponse(
                quizs = pageQuizs.map { AdminQuizResponse.from(it) },
                nextId = getNextId(quizs, pageQuizs, size) { it.id },
            )
        }

        fun fromNotApprovedQuizs(
            quizs: List<NotApprovedQuiz>,
            size: Int,
        ): AdminQuizsResponse {
            val pageQuizs = quizs.take(size)

            return AdminQuizsResponse(
                quizs = pageQuizs.map { AdminQuizResponse.from(it) },
                nextId = getNextId(quizs, pageQuizs, size) { it.id },
            )
        }
    }
}

data class AdminQuizResponse(
    val id: String,
    val userId: String,
    val level: Level,
    val category: Category,
    val language: Language,
    val problem: String,
    val expectedAnswer: String,
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss",
        timezone = "UTC",
    )
    val createdAt: LocalDateTime,
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss",
        timezone = "UTC",
    )
    val modifiedAt: LocalDateTime,
) {

    companion object {
        fun from(quiz: Quiz): AdminQuizResponse {
            return AdminQuizResponse(
                id = quiz.id.toString(),
                userId = quiz.userId.toString(),
                level = quiz.level,
                category = quiz.category,
                language = quiz.language,
                problem = quiz.problem,
                expectedAnswer = quiz.expectedAnswer,
                createdAt = quiz.createdAt.toUtcLocalDateTime(),
                modifiedAt = (quiz.modifiedAt ?: quiz.createdAt).toUtcLocalDateTime(),
            )
        }

        fun from(quiz: NotApprovedQuiz): AdminQuizResponse {
            return AdminQuizResponse(
                id = quiz.id.toString(),
                userId = quiz.userId.toString(),
                level = quiz.level,
                category = quiz.category,
                language = quiz.language,
                problem = quiz.problem,
                expectedAnswer = quiz.expectedAnswer,
                createdAt = quiz.createdAt.toUtcLocalDateTime(),
                modifiedAt = (quiz.modifiedAt ?: quiz.createdAt).toUtcLocalDateTime(),
            )
        }
    }
}

data class AdminQuizSolveContextsResponse(
    val quizSolveContexts: List<AdminQuizSolveContextResponse>,
    val nextId: String?,
) {

    companion object {
        fun from(
            quizSolveContexts: List<QuizSolveContext>,
            size: Int,
        ): AdminQuizSolveContextsResponse {
            val pageQuizSolveContexts = quizSolveContexts.take(size)

            return AdminQuizSolveContextsResponse(
                quizSolveContexts = pageQuizSolveContexts.map { AdminQuizSolveContextResponse.from(it) },
                nextId = getNextId(quizSolveContexts, pageQuizSolveContexts, size) { it.id },
            )
        }
    }
}

data class AdminQuizSolveContextResponse(
    val id: String,
    val userId: String,
    val category: Category,
    val round: Round,
    val prize: Int,
    val solvedAt: LocalDate,
    val status: QuizSolveContextStatus,
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss",
        timezone = "UTC",
    )
    val createdAt: LocalDateTime,
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss",
        timezone = "UTC",
    )
    val modifiedAt: LocalDateTime,
) {

    data class Round(
        val total: Int,
        val current: Int,
        @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "UTC",
        )
        val timeoutAt: LocalDateTime?,
    )

    companion object {
        fun from(quizSolveContext: QuizSolveContext): AdminQuizSolveContextResponse {
            return AdminQuizSolveContextResponse(
                id = quizSolveContext.id.toString(),
                userId = quizSolveContext.userId.toString(),
                category = quizSolveContext.category,
                round = Round(
                    total = quizSolveContext.solveStage.maxSolveStage,
                    current = quizSolveContext.solveStage.getCurrentStage(),
                    timeoutAt = quizSolveContext.solveStage.getCurrentStageTimeout()?.toUtcLocalDateTime(),
                ),
                prize = quizSolveContext.getPrize(),
                solvedAt = quizSolveContext.solvedAt,
                status = quizSolveContext.getStatus(),
                createdAt = quizSolveContext.createdAt.toUtcLocalDateTime(),
                modifiedAt = (quizSolveContext.modifiedAt ?: quizSolveContext.createdAt).toUtcLocalDateTime(),
            )
        }
    }
}

private fun Instant.toUtcLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(this, ZoneOffset.UTC)

private fun <T> getNextId(
    allItems: List<T>,
    pageItems: List<T>,
    size: Int,
    idSelector: (T) -> Long,
): String? {
    if (allItems.size <= size || pageItems.isEmpty()) {
        return null
    }

    return idSelector(pageItems.last()).toString()
}
