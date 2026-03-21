package org.gitanimals.quiz.controller.admin

import org.gitanimals.core.admin.AdminCallDetected
import org.gitanimals.core.admin.AdminConst.ADMIN_SECRET_KEY
import org.gitanimals.identity.app.Token
import org.gitanimals.identity.app.UserFacade
import org.gitanimals.quiz.app.ApproveQuizFacade
import org.gitanimals.quiz.app.DeleteQuizFacade
import org.gitanimals.quiz.app.DenyQuizFacade
import org.gitanimals.quiz.controller.admin.request.QuizAdminActionRequest
import org.gitanimals.quiz.controller.admin.response.AdminQuizSolveContextsResponse
import org.gitanimals.quiz.controller.admin.response.AdminQuizsResponse
import org.gitanimals.quiz.domain.approved.QuizService
import org.gitanimals.quiz.domain.context.QuizSolveContextService
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language
import org.gitanimals.quiz.domain.core.Level
import org.gitanimals.quiz.domain.not_approved.NotApprovedQuizService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/quizs")
class QuizAdminController(
    private val quizService: QuizService,
    private val quizSolveContextService: QuizSolveContextService,
    private val notApprovedQuizService: NotApprovedQuizService,
    private val approveQuizFacade: ApproveQuizFacade,
    private val denyQuizFacade: DenyQuizFacade,
    private val deleteQuizFacade: DeleteQuizFacade,
    private val userFacade: UserFacade,
    private val eventPublisher: ApplicationEventPublisher,
    @Value("\${gitanimals.admin.token}") private val adminToken: String,
    @Value("\${quiz.approve.token}") private val approveToken: String,
) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/approved")
    fun scrollApprovedQuizs(
        @RequestParam(name = "lastId", required = false) lastId: Long?,
        @RequestParam(name = "level", required = false) level: Level?,
        @RequestParam(name = "category", required = false) category: Category?,
        @RequestParam(name = "language", required = false) language: Language?,
        @RequestHeader(ADMIN_SECRET_KEY) adminSecret: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String,
    ): AdminQuizsResponse {
        validateAdminAccess(adminSecret, authorization)

        return AdminQuizsResponse.fromApprovedQuizs(
            quizs = quizService.scrollApprovedQuizs(
                lastId = lastId ?: Long.MAX_VALUE,
                level = level,
                category = category,
                language = language,
                size = SCROLL_QUERY_SIZE + 1,
            ),
            size = SCROLL_QUERY_SIZE,
        )
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/contexts")
    fun scrollQuizSolveContexts(
        @RequestParam("userId") userId: Long,
        @RequestParam(name = "lastId", required = false) lastId: Long?,
        @RequestHeader(ADMIN_SECRET_KEY) adminSecret: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String,
    ): AdminQuizSolveContextsResponse {
        validateAdminAccess(adminSecret, authorization)

        return AdminQuizSolveContextsResponse.from(
            quizSolveContexts = quizSolveContextService.scrollQuizSolveContexts(
                userId = userId,
                lastId = lastId ?: Long.MAX_VALUE,
                size = SCROLL_QUERY_SIZE + 1,
            ),
            size = SCROLL_QUERY_SIZE,
        )
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/not-approved")
    fun scrollNotApprovedQuizs(
        @RequestParam(name = "lastId", required = false) lastId: Long?,
        @RequestParam(name = "level", required = false) level: Level?,
        @RequestParam(name = "category", required = false) category: Category?,
        @RequestParam(name = "language", required = false) language: Language?,
        @RequestHeader(ADMIN_SECRET_KEY) adminSecret: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String,
    ): AdminQuizsResponse {
        validateAdminAccess(adminSecret, authorization)

        return AdminQuizsResponse.fromNotApprovedQuizs(
            quizs = notApprovedQuizService.scrollNotApprovedQuizs(
                lastId = lastId ?: Long.MAX_VALUE,
                level = level,
                category = category,
                language = language,
                size = SCROLL_QUERY_SIZE + 1,
            ),
            size = SCROLL_QUERY_SIZE,
        )
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/not-approved/{quizId}")
    fun deleteNotApprovedQuiz(
        @PathVariable("quizId") quizId: Long,
        @RequestHeader(ADMIN_SECRET_KEY) adminSecret: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String,
        @RequestBody request: QuizAdminActionRequest,
    ) {
        val adminUser = getAdminUser(adminSecret, authorization)

        denyQuizFacade.notApprovedQuiz(
            approveToken = approveToken,
            notApprovedQuizId = quizId,
        )
        publishAdminCallDetected(
            username = adminUser.getName(),
            reason = request.reason,
            path = DELETE_NOT_APPROVED_QUIZ_PATH,
            description = "미승인 퀴즈 $quizId 삭제 및 포인트 회수",
        )
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/not-approved/{quizId}/approve")
    fun approveNotApprovedQuiz(
        @PathVariable("quizId") quizId: Long,
        @RequestHeader(ADMIN_SECRET_KEY) adminSecret: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String,
        @RequestBody request: QuizAdminActionRequest,
    ) {
        val adminUser = getAdminUser(adminSecret, authorization)

        approveQuizFacade.approveQuiz(
            approveToken = approveToken,
            notApprovedQuizId = quizId,
        )
        publishAdminCallDetected(
            username = adminUser.getName(),
            reason = request.reason,
            path = APPROVE_NOT_APPROVED_QUIZ_PATH,
            description = "미승인 퀴즈 $quizId 승인",
        )
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/approved/{quizId}")
    fun deleteApprovedQuiz(
        @PathVariable("quizId") quizId: Long,
        @RequestHeader(ADMIN_SECRET_KEY) adminSecret: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String,
        @RequestBody request: QuizAdminActionRequest,
    ) {
        val adminUser = getAdminUser(adminSecret, authorization)

        deleteQuizFacade.deleteQuizById(
            approveToken = approveToken,
            quizId = quizId,
        )
        publishAdminCallDetected(
            username = adminUser.getName(),
            reason = request.reason,
            path = DELETE_APPROVED_QUIZ_PATH,
            description = "승인 퀴즈 $quizId 삭제",
        )
    }

    private fun validateAdminAccess(
        adminSecret: String,
        authorization: String,
    ) {
        requireAdminSecret(adminSecret)
        userFacade.getUserByToken(Token.from(authorization))
    }

    private fun getAdminUser(
        adminSecret: String,
        authorization: String,
    ) = run {
        requireAdminSecret(adminSecret)
        userFacade.getUserByToken(Token.from(authorization))
    }

    private fun requireAdminSecret(adminSecret: String) {
        require(adminSecret == adminToken) {
            "WRONG TOKEN"
        }
    }

    private fun publishAdminCallDetected(
        username: String,
        reason: String,
        path: String,
        description: String,
    ) {
        eventPublisher.publishEvent(
            AdminCallDetected(
                username = username,
                reason = reason,
                path = path,
                description = description,
            )
        )
    }

    companion object {
        private const val SCROLL_QUERY_SIZE = 20
        private const val DELETE_NOT_APPROVED_QUIZ_PATH = "/admin/quizs/not-approved/{quizId}"
        private const val APPROVE_NOT_APPROVED_QUIZ_PATH = "/admin/quizs/not-approved/{quizId}/approve"
        private const val DELETE_APPROVED_QUIZ_PATH = "/admin/quizs/approved/{quizId}"
    }
}
