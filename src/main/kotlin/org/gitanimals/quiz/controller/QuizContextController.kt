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
        @RequestHeader("Locale") locale: String,
    ): CreateQuizContextResponse {
        val contextId = solveQuizFacade.createContext(token, locale, createSolveQuizRequest)
        return CreateQuizContextResponse(contextId.toString())
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/quizs/context/{contextId}")
    fun getAndStartSolveQuizContextById(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("contextId") contextId: Long,
    ): QuizContextResponse = solveQuizFacade.getAndStartSolveQuizContextById(token, contextId)

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/quizs/context/{contextId}/answers")
    fun answerQuizByContextId(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("contextId") contextId: Long,
        @RequestBody answerQuizRequest: AnswerQuizRequest,
    ) = solveQuizFacade.answerQuizById(token, contextId, answerQuizRequest.answer)

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/quizs/context/{contextId}/results")
    fun getQuizSolveContextStatus(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("contextId") contextId: Long,
    ): QuizSolveContextStatusResponse {
        val quizContext = solveQuizFacade.getQuizById(token = token, id = contextId)

        return QuizSolveContextStatusResponse(
            prize = quizContext.getPrize(),
            result = quizContext.getStatus(),
        )
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/quizs/context/{contextId}/stops")
    fun stopQuizByContextId(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @PathVariable("contextId") contextId: Long,
    ) = solveQuizFacade.stopQuiz(token = token, id = contextId)


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/quizs/context/today")
    fun getTodaySolvedContextResult(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
    ) = solveQuizFacade.getTodaySolvedContextResult(token)
}
