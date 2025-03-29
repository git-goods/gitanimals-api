package org.gitanimals.quiz.domain.not_approved

import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Level
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotApprovedQuizService(
    private val notApprovedQuizRepository: NotApprovedQuizRepository,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    @Transactional
    fun createNotApprovedQuiz(
        userId: Long,
        problem: String,
        level: Level,
        category: Category,
        expectedAnswer: String,
    ): NotApprovedQuiz {
        val notApprovedQuizs = notApprovedQuizRepository.findAllByUserId(userId)

        require(notApprovedQuizs.size < 3) {
            val message =
                "Cannot create quiz cause already have ${notApprovedQuizs.size} not approved quizs."
            logger.info(message)
            message
        }

        return notApprovedQuizRepository.save(
            NotApprovedQuiz.create(
                userId = userId,
                problem = problem,
                level = level,
                category = category,
                expectedAnswer = expectedAnswer,
            )
        )
    }

    fun getById(notApprovedQuizId: Long): NotApprovedQuiz =
        notApprovedQuizRepository.getReferenceById(notApprovedQuizId)

    fun denyQuizById(notApprovedQuizId: Long) =
        notApprovedQuizRepository.deleteById(notApprovedQuizId)
}
