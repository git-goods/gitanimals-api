package org.gitanimals.quiz.app

import org.gitanimals.core.IdGenerator
import org.gitanimals.quiz.app.CreateQuizFacade.Companion.CREATE_QUIZ_PRICE
import org.gitanimals.quiz.domain.not_approved.NotApprovedQuizService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DenyQuizFacade(
    private val identityApi: IdentityApi,
    private val notApprovedQuizService: NotApprovedQuizService,
    @Value("\${quiz.approve.token}") private val approveToken: String,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    fun notApprovedQuiz(approveToken: String, notApprovedQuizId: Long) {
        require(approveToken == this.approveToken) {
            val message = "Cannot approve token cause invalid approve token."
            logger.info(message)
            message
        }

        val notApprovedQuiz = runCatching {
            notApprovedQuizService.getById(notApprovedQuizId)
        }.getOrElse { return }

        notApprovedQuizService.deleteQuizById(notApprovedQuizId)

        runCatching {
            identityApi.decreaseUserPointsById(
                userId = notApprovedQuiz.userId,
                point = CREATE_QUIZ_PRICE,
                idempotencyKey = IdGenerator.generate().toString(),
            )
        }.getOrElse {
            logger.error("[DenyQuizFacade] Cannot decrease point. userId: \"${notApprovedQuiz.userId}\", point: \"$CREATE_QUIZ_PRICE\"")
        }
    }
}
