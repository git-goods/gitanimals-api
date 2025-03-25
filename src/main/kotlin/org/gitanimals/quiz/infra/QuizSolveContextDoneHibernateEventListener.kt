package org.gitanimals.quiz.infra

import org.gitanimals.core.IdGenerator
import org.gitanimals.inbox.domain.InboxType
import org.gitanimals.quiz.app.IdentityApi
import org.gitanimals.quiz.app.InboxApi
import org.gitanimals.quiz.app.InboxApi.InboxInputRequest
import org.gitanimals.quiz.app.InboxApi.InboxInputRequest.InboxData
import org.gitanimals.quiz.domain.context.QuizSolveContext
import org.gitanimals.quiz.domain.context.QuizSolveContextStatus
import org.hibernate.event.spi.PostUpdateEvent
import org.hibernate.event.spi.PostUpdateEventListener
import org.hibernate.persister.entity.EntityPersister
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class QuizSolveContextDoneHibernateEventListener(
    private val inboxApi: InboxApi,
    private val identityApi: IdentityApi,
) : PostUpdateEventListener {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun requiresPostCommitHandling(persister: EntityPersister): Boolean =
        persister.mappedClass == QuizSolveContext::class.java

    override fun onPostUpdate(event: PostUpdateEvent) {
        if (event.entity is QuizSolveContext) {
            val quizSolveContext = event.entity as QuizSolveContext
            if (quizSolveContext.getStatus() == QuizSolveContextStatus.DONE) {
                runCatching {
                    identityApi.increaseUserPointsById(
                        userId = quizSolveContext.userId,
                        point = quizSolveContext.getPrize().toLong(),
                        idempotencyKey = IdGenerator.generate().toString(),
                    )
                }.onSuccess {
                    inboxApi.inputInbox(
                        InboxInputRequest(
                            inboxData = InboxData(
                                userId = quizSolveContext.userId,
                                type = InboxType.INBOX,
                                title = "Quiz prize arrived.",
                                body = "Congratulations! You got ${quizSolveContext.getPrize()} point by solving quiz.",
                                image = "https://avatars.githubusercontent.com/u/171903401?s=200&v=4",
                                redirectTo = "NO_REDIRECT",
                            )
                        )
                    )
                }.onFailure {
                    logger.error(
                        "Cannot give point to user. userId: \"${quizSolveContext.userId}\", missing point: \"${quizSolveContext.getPrize()}\"",
                        it
                    )
                }
            }
        }
    }
}
