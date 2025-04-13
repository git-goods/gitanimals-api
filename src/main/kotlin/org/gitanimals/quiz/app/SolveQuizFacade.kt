package org.gitanimals.quiz.app

import org.gitanimals.core.auth.InternalAuth
import org.gitanimals.quiz.app.request.CreateSolveQuizRequest
import org.gitanimals.quiz.app.response.QuizContextResponse
import org.gitanimals.quiz.app.response.TodaySolvedContextResponse
import org.gitanimals.quiz.domain.approved.QuizService
import org.gitanimals.quiz.domain.context.QuizSolveContext
import org.gitanimals.quiz.domain.context.QuizSolveContextService
import org.gitanimals.quiz.domain.core.Language
import org.gitanimals.quiz.domain.core.Level
import org.springframework.stereotype.Service

@Service
class SolveQuizFacade(
    private val internalAuth: InternalAuth,
    private val quizService: QuizService,
    private val quizSolveContextService: QuizSolveContextService,
) {

    fun createContext(locale: String, request: CreateSolveQuizRequest): Long {
        val userId = internalAuth.getUserId()

        val language = when (locale.uppercase()) {
            "EN_US" -> Language.ENGLISH
            else -> Language.KOREA
        }
        val pickedQuizs = quizService.findAllQuizByLevelAndCategoryAndLanguage(
            levels = quizLevels,
            language = language,
            category = request.category,
        )


        val quizSolveContext = quizSolveContextService.createQuizSolveContext(
            userId = userId,
            category = request.category,
            quizs = pickedQuizs,
        )

        return quizSolveContext.id
    }

    fun getAndStartSolveQuizContextById(id: Long): QuizContextResponse {
        val userId = internalAuth.getUserId()

        val quizSolveContext =
            quizSolveContextService.getAndStartSolveQuizContext(id = id, userId = userId)

        return QuizContextResponse.from(quizSolveContext)
    }

    fun answerQuizById(id: Long, answer: String) {
        val userId = internalAuth.getUserId()

        quizSolveContextService.solveQuiz(id, userId, answer)
    }

    fun getQuizById(id: Long): QuizSolveContext {
        val userId = internalAuth.getUserId()
        return quizSolveContextService.getQuizSolveContextByIdAndUserId(id = id, userId = userId)
    }

    fun stopQuiz(id: Long) {
        val userId = internalAuth.getUserId()

        quizSolveContextService.stopQuizByIdAndUserId(id = id, userId = userId)
    }

    fun getTodaySolvedContextResult(token: String): TodaySolvedContextResponse {
        val userId = internalAuth.getUserId()
        val quizSolveContext = quizSolveContextService.findTodaySolvedContext(userId)

        return TodaySolvedContextResponse.from(quizSolveContext)
    }

    private companion object {
        private val quizLevels =
            listOf(Level.EASY, Level.EASY, Level.MEDIUM, Level.DIFFICULT, Level.DIFFICULT)
    }
}
