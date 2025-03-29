package org.gitanimals.quiz.infra.hibernate

import org.gitanimals.quiz.domain.approved.Quiz
import org.gitanimals.quiz.infra.similarity.QuizSimilarityRepository
import org.hibernate.event.spi.PostDeleteEvent
import org.hibernate.event.spi.PostDeleteEventListener
import org.hibernate.persister.entity.EntityPersister
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class QuizDeletedHibernateEventListener(
    private val quizSimilarityRepository: QuizSimilarityRepository,
) : PostDeleteEventListener {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun requiresPostCommitHandling(persister: EntityPersister): Boolean =
        persister.mappedClass == Quiz::class.java

    override fun onPostDelete(event: PostDeleteEvent) {
        if (event.entity is Quiz) {
            val quiz = event.entity as Quiz
            runCatching {
                quizSimilarityRepository.deleteByQuizId(quiz.id)
            }.getOrElse {
                logger.error("Cannot delete quiz similarity must fix it. quizId: \"${quiz.id}\"")
            }
        }
    }
}
