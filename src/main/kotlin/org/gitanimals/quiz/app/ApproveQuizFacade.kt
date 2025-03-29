package org.gitanimals.quiz.app

import org.gitanimals.quiz.domain.approved.QuizService
import org.gitanimals.quiz.domain.not_approved.NotApprovedQuizService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ApproveQuizFacade(
    private val quizService: QuizService,
    private val notApprovedQuizService: NotApprovedQuizService,
    @Value("\${quiz.approve.token}") private val approveToken: String,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    fun approveQuiz(approveToken: String, notApprovedQuizId: Long) {
        require(approveToken == this.approveToken) {
            val message = "Cannot approve token cause invalid approve token."
            logger.info(message)
            message
        }

        val notApprovedQuiz = notApprovedQuizService.getById(notApprovedQuizId)

        val quiz = notApprovedQuiz.toQuiz()

        runCatching {
            quizService.createNewQuiz(quiz)
        }.onSuccess {
            notApprovedQuizService.deleteQuizById(notApprovedQuizId)
        }.getOrElse {
            logger.error("Cannot approve quiz. cause $it", it)
            throw it
        }
    }
}
