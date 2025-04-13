package org.gitanimals.quiz.controller

import org.gitanimals.quiz.app.CreateQuizFacade
import org.gitanimals.quiz.app.request.CreateQuizRequest
import org.gitanimals.quiz.app.response.CreateQuizResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class QuizController(
    private val createQuizFacade: CreateQuizFacade,
) {

    @PostMapping("/quizs")
    fun createQuiz(
        @RequestBody request: CreateQuizRequest,
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
    ): CreateQuizResponse = createQuizFacade.createQuiz(request)
}
