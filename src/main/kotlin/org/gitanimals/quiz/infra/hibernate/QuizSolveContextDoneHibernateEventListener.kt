package org.gitanimals.quiz.infra.hibernate

import org.gitanimals.core.GracefulShutdownDispatcher.gracefulLaunch
import org.gitanimals.core.IdGenerator
import org.gitanimals.core.clock
import org.gitanimals.core.event.EventLogger
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
                        contextId = quizSolveContext.id,
                        userId = quizSolveContext.userId,
                        prize = quizSolveContext.getPrize(),
                        status = quizSolveContext.getStatus(),
                        language = quizSolveContext.category.name,
                    )
                )
            }
        }
    }

@Component
class QuizSolveContextDoneLogicDelegator(
    private val inboxApi: InboxApi,
    private val identityApi: IdentityApi,
    private val eventLogger: EventLogger,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    data class QuizSolveContextDone(
        val contextId: Long,
        val userId: Long,
        val prize: Int,
        val status: QuizSolveContextStatus,
        val language: String,
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

                runCatching {
                    eventLogger.track(
                        eventName = "complete_solve_quiz",
                        distinctId = event.userId.toString(),
                        properties = mapOf(
                            "context_id" to event.contextId,
                            "score" to event.prize,
                            "language" to event.language,
                            "user_id" to event.userId,
                        )
                    )
                }.onFailure {
                    logger.warn("Failed to track complete_solve_quiz event. cause ${it.message}", it)
                }
            }
        }
    }
}
