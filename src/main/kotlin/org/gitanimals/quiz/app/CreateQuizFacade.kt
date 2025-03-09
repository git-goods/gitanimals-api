package org.gitanimals.quiz.app

import org.gitanimals.quiz.app.request.CreateQuizRequest
import org.gitanimals.quiz.domain.QuizService
import org.springframework.stereotype.Component

@Component
class CreateQuizFacade(
    private val identityApi: IdentityApi,
    private val quizService: QuizService,
) {

    fun createQuiz(token: String, createQuizRequest: CreateQuizRequest) {
        val user = identityApi.getUserByToken(token)

        quizService.createNewQuiz(
            userId = user.id.toLong(),
            problem = createQuizRequest.problem,
            category = createQuizRequest.category,
            expectedAnswer = createQuizRequest.expectedAnswer,
            level = createQuizRequest.level,
        )
    }
}
