package org.gitanimals.quiz.infra.hibernate

import org.gitanimals.core.GracefulShutdownDispatcher.gracefulLaunch
import org.gitanimals.core.IdGenerator
import org.gitanimals.core.clock
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
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class QuizSolveContextDoneHibernateEventListener(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : PostUpdateEventListener {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun requiresPostCommitHandling(persister: EntityPersister): Boolean =
        persister.mappedClass == QuizSolveContext::class.java

    override fun onPostUpdate(event: PostUpdateEvent) {
            val quizSolveContext = event.entity as QuizSolveContext
            logger.info("[QuizSolveContextDoneHibernateEventListener] userId: ${quizSolveContext.userId}, prize: ${quizSolveContext.getPrize()}, status: ${quizSolveContext.getStatus()}")
            if (quizSolveContext.getStatus() == QuizSolveContextStatus.DONE) {
                applicationEventPublisher.publishEvent(
                    QuizSolveContextDoneLogicDelegator.QuizSolveContextDone(
                        userId = quizSolveContext.userId,
                        prize = quizSolveContext.getPrize(),
                        status = quizSolveContext.getStatus(),
                    )
                )
            }
        }
    }

@Component
class QuizSolveContextDoneLogicDelegator(
    private val inboxApi: InboxApi,
    private val identityApi: IdentityApi,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    data class QuizSolveContextDone(
        val userId: Long,
        val prize: Int,
        val status: QuizSolveContextStatus,
    )

    @EventListener(QuizSolveContextDone::class)
    fun listenQuizSolveContextDone(event: QuizSolveContextDone) {
        gracefulLaunch {
            if (event.status == QuizSolveContextStatus.DONE) {
                runCatching {
                    identityApi.increaseUserPointsById(
                        userId = event.userId,
                        point = event.prize.toLong(),
                        idempotencyKey = IdGenerator.generate().toString(),
                    )
                }.onSuccess {
                    inboxApi.inputInbox(
                        userId = event.userId,
                        request = InboxInputRequest(
                            publisher = InboxInputRequest.Publisher(
                                publisher = "QUIZ",
                                publishedAt = clock.instant(),
                            ),
                            inboxData = InboxData(
                                userId = event.userId,
                                type = InboxType.INBOX,
                                title = "Quiz prize arrived.",
                                body = "Congratulations! You got ${event.prize} point by solving quiz.",
                                image = "https://avatars.githubusercontent.com/u/171903401?s=200&v=4",
                                redirectTo = "NO_REDIRECT",
                            )
                        ),
                    )
                }.onFailure {
                    logger.error(
                        "Cannot give point to user. userId: \"${event.userId}\", missing point: \"${event.prize}\"",
                        it
                    )
                }
            }
        }
    }
}
