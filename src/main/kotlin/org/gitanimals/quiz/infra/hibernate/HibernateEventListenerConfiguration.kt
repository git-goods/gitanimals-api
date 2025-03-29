package org.gitanimals.quiz.infra.hibernate

import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManagerFactory
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.internal.SessionFactoryImpl
import org.springframework.context.annotation.Configuration

@Configuration
class HibernateEventListenerConfiguration(
    private val entityManagerFactory: EntityManagerFactory,
    private val newQuizCreatedInsertEventListener: NewQuizCreatedInsertHibernateEventListener,
    private val quizSolveContextDoneHibernateEventListener: QuizSolveContextDoneHibernateEventListener,
    private val quizDeletedHibernateEventListener: QuizDeletedHibernateEventListener,
) {

    @PostConstruct
    fun newQuizCreatedInsertEventListener() {
        val sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl::class.java)

        val eventListenerRegistry =
            sessionFactory.serviceRegistry.getService(EventListenerRegistry::class.java)!!

        eventListenerRegistry.appendListeners(
            EventType.POST_COMMIT_INSERT,
            newQuizCreatedInsertEventListener,
        )
        eventListenerRegistry.appendListeners(
            EventType.POST_COMMIT_UPDATE,
            quizSolveContextDoneHibernateEventListener,
        )
        eventListenerRegistry.appendListeners(
            EventType.POST_COMMIT_DELETE,
            quizDeletedHibernateEventListener,
        )
    }
}
