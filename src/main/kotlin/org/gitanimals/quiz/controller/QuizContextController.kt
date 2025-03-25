package org.gitanimals.quiz.controller

import org.apache.http.HttpHeaders
import org.gitanimals.quiz.app.SolveQuizFacade
import org.gitanimals.quiz.app.request.CreateSolveQuizRequest
import org.gitanimals.quiz.app.response.QuizContextResponse
import org.gitanimals.quiz.controller.request.AnswerQuizRequest
import org.gitanimals.quiz.controller.response.CreateQuizContextResponse
import org.gitanimals.quiz.controller.response.QuizSolveContextStatusResponse
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/quizs/context/{contextId}")
    fun getAndStartSolveQuizContextById(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("contextId") contextId: Long,
    ): QuizContextResponse = solveQuizFacade.getAndStartSolveQuizContextById(token, contextId)

    @ResponseStatus(HttpStatus.OK)
    fun answerQuizByContextId(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("contextId") contextId: Long,
        @RequestBody answerQuizRequest: AnswerQuizRequest,
    ) = solveQuizFacade.answerQuizById(token, contextId, answerQuizRequest.answer)

    @ResponseStatus(HttpStatus.OK)
    fun getQuizSolveContextStatus(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("contextId") contextId: Long,
    ): QuizSolveContextStatusResponse = QuizSolveContextStatusResponse(
        solveQuizFacade.getQuizById(token = token, id = contextId).getStatus()
    )
}
