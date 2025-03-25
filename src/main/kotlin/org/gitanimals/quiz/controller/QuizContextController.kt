package org.gitanimals.quiz.controller

import org.apache.http.HttpHeaders
import org.gitanimals.quiz.app.SolveQuizFacade
import org.gitanimals.quiz.app.request.CreateSolveQuizRequest
import org.gitanimals.quiz.controller.response.CreateQuizContextResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class QuizContextController(
    private val solveQuizFacade: SolveQuizFacade,
) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/quizs/context")
    fun createQuizContext(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestBody createSolveQuizRequest: CreateSolveQuizRequest,
    ): CreateQuizContextResponse {
        val contextId = solveQuizFacade.createContext(token, createSolveQuizRequest)
        return CreateQuizContextResponse(contextId.toString())
    }
}
