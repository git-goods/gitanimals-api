package org.gitanimals.quiz.app

import org.gitanimals.quiz.app.request.CreateSolveQuizRequest
import org.gitanimals.quiz.app.response.QuizContextResponse
import org.gitanimals.quiz.domain.approved.QuizService
import org.gitanimals.quiz.domain.context.QuizSolveContext
import org.gitanimals.quiz.domain.context.QuizSolveContextService
import org.gitanimals.quiz.domain.core.Level
import org.springframework.stereotype.Service

@Service
class SolveQuizFacade(
    private val identityApi: IdentityApi,
    private val quizService: QuizService,
    private val quizSolveContextService: QuizSolveContextService,
) {

    fun createContext(token: String, request: CreateSolveQuizRequest): Long {
        val pickedQuizs = quizService.findAllQuizByLevel(quizLevels)
        val user = identityApi.getUserByToken(token)

        val quizSolveContext = quizSolveContextService.createQuizSolveContext(
            userId = user.id.toLong(),
            category = request.category,
            quizs = pickedQuizs,
        )

        return quizSolveContext.id
    }

    fun getAndStartSolveQuizContextById(token: String, id: Long): QuizContextResponse {
        val user = identityApi.getUserByToken(token)

        val quizSolveContext =
            quizSolveContextService.getAndStartSolveQuizContext(id = id, userId = user.id.toLong())

        return QuizContextResponse.from(quizSolveContext)
    }

    fun answerQuizById(token: String, id: Long, answer: String) {
        val user = identityApi.getUserByToken(token)

        quizSolveContextService.solveQuiz(id, user.id.toLong(), answer)
    }

    fun getQuizById(token: String, id: Long): QuizSolveContext {
        val userId = identityApi.getUserByToken(token).id.toLong()
        return quizSolveContextService.getQuizSolveContextByIdAndUserId(id = id, userId = userId)
    }

    private companion object {
        private val quizLevels =
            listOf(Level.EASY, Level.EASY, Level.MEDIUM, Level.DIFFICULT, Level.DIFFICULT)
    }
}
