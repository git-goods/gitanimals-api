package org.gitanimals.quiz.app

import org.gitanimals.quiz.domain.approved.QuizService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DeleteQuizFacade(
    val quizService: QuizService,
    @Value("\${quiz.approve.token}") private val approveToken: String,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    fun deleteQuizById(approveToken: String, quizId: Long) {
        require(approveToken == this.approveToken) {
            val message = "Cannot approve token cause invalid approve token."
            logger.info(message)
            message
        }

        quizService.deleteById(quizId)
    }
}
