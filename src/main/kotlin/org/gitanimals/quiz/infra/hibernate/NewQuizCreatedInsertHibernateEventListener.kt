package org.gitanimals.quiz.infra.hibernate

import org.gitanimals.quiz.domain.approved.Quiz
import org.gitanimals.quiz.infra.event.NewQuizCreated
import org.hibernate.event.spi.PostInsertEvent
import org.hibernate.event.spi.PostInsertEventListener
import org.hibernate.persister.entity.EntityPersister
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class NewQuizCreatedInsertHibernateEventListener(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : PostInsertEventListener {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun requiresPostCommitHandling(persister: EntityPersister): Boolean =
        persister.mappedClass == Quiz::class.java

    override fun onPostInsert(event: PostInsertEvent) {
        if (event.entity is Quiz) {
            runCatching {
                applicationEventPublisher.publishEvent(
                    NewQuizCreated.from(event.entity as Quiz)
                )
            }.onFailure {
                logger.error("Cannot publish NewQuizCreate event. cause ${it.message}", it)
            }
        }
    }
}
