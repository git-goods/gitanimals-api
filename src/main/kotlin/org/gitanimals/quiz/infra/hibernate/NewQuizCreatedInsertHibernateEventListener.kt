package org.gitanimals.quiz.infra.hibernate

import org.gitanimals.core.event.EventLogger
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
    private val eventLogger: EventLogger,
) : PostInsertEventListener {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun requiresPostCommitHandling(persister: EntityPersister): Boolean =
        persister.mappedClass == Quiz::class.java

    override fun onPostInsert(event: PostInsertEvent) {
        if (event.entity is Quiz) {
            val quiz = event.entity as Quiz
            runCatching {
                applicationEventPublisher.publishEvent(
                    NewQuizCreated.from(quiz)
                )
            }.onFailure {
                logger.error("Cannot publish NewQuizCreate event. cause ${it.message}", it)
            }
            eventLogger.track(
                eventName = "complete_make_quiz",
                distinctId = quiz.userId.toString(),
                properties = mapOf(
                    "quiz_id" to quiz.id,
                    "language" to quiz.language.name,
                    "user_id" to quiz.userId,
                )
            )
        }
    }
}
