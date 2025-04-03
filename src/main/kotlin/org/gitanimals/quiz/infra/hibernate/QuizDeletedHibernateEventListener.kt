package org.gitanimals.quiz.infra.hibernate

import org.gitanimals.core.GracefulShutdownDispatcher.gracefulLaunch
import org.gitanimals.quiz.domain.approved.Quiz
import org.gitanimals.quiz.infra.similarity.QuizSimilarityRepository
import org.hibernate.event.spi.PostDeleteEvent
import org.hibernate.event.spi.PostDeleteEventListener
import org.hibernate.persister.entity.EntityPersister
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class QuizDeletedHibernateEventListener(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : PostDeleteEventListener {

    override fun requiresPostCommitHandling(persister: EntityPersister): Boolean =
        persister.mappedClass == Quiz::class.java

    override fun onPostDelete(event: PostDeleteEvent) {
        if (event.entity is Quiz) {
            val quiz = event.entity as Quiz
            applicationEventPublisher.publishEvent(QuizDeleteLogicDelegator.QuizDeleted(quiz.id))
        }
    }
}

@Component
class QuizDeleteLogicDelegator(
    private val quizSimilarityRepository: QuizSimilarityRepository,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    data class QuizDeleted(
        val quizId: Long,
    )

    @EventListener(QuizDeleted::class)
    fun listenQuizDeleted(event: QuizDeleted) {
        gracefulLaunch {
            runCatching {
                quizSimilarityRepository.deleteByQuizId(event.quizId)
            }.getOrElse {
                logger.error("Cannot delete quiz similarity must fix it. quizId: \"${event.quizId}\"")
            }
        }
    }
}
