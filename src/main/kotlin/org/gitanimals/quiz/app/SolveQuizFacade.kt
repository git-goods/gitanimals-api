package org.gitanimals.quiz.app

import org.gitanimals.quiz.app.request.CreateSolveQuizRequest
import org.gitanimals.quiz.domain.approved.QuizService
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

    private companion object {
        private val quizLevels =
            listOf(Level.EASY, Level.EASY, Level.MEDIUM, Level.DIFFICULT, Level.DIFFICULT)
    }
}
