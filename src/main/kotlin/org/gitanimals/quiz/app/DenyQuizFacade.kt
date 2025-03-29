package org.gitanimals.quiz.app

import org.gitanimals.quiz.domain.not_approved.NotApprovedQuizService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DenyQuizFacade(
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

        notApprovedQuizService.denyQuizById(notApprovedQuizId)
    }
}
