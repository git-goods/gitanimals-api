package org.gitanimals.quiz.app

import org.gitanimals.core.IdGenerator
import org.gitanimals.core.TraceIdContextOrchestrator
import org.gitanimals.core.TraceIdContextRollback
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.quiz.app.event.NotApprovedQuizCreated
import org.gitanimals.quiz.app.request.CreateQuizRequest
import org.gitanimals.quiz.domain.NotApprovedQuizService
import org.gitanimals.quiz.domain.QuizService
import org.gitanimals.quiz.domain.prompt.QuizCreatePromptService
import org.rooftop.netx.api.Orchestrator
import org.rooftop.netx.api.OrchestratorFactory
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class CreateQuizFacade(
    private val aiApi: AIApi,
    private val identityApi: IdentityApi,
    private val quizService: QuizService,
    private val notApprovedQuizService: NotApprovedQuizService,
    private val textSimilarityChecker: TextSimilarityChecker,
    private val eventPublisher: ApplicationEventPublisher,
    private val quizCreatePromptService: QuizCreatePromptService,
    orchestratorFactory: OrchestratorFactory,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    private lateinit var createQuizOrchestrator: Orchestrator<CreateQuizRequest, Unit>

    fun createQuiz(token: String, createQuizRequest: CreateQuizRequest) {
        val user = identityApi.getUserByToken(token)

        val similarityResponses = textSimilarityChecker.getSimilarity(createQuizRequest.problem)
        if (similarityResponses.similarityQuizIds.isNotEmpty()) {
            logger.info("Cannot pass similarity check, need to human intelligence.")

            val similarityQuizs = quizService.findAllByIds(similarityResponses.similarityQuizIds)

            val notApprovedQuiz = notApprovedQuizService.createNotApprovedQuiz(
                userId = user.id.toLong(),
                problem = createQuizRequest.problem,
                category = createQuizRequest.category,
                expectedAnswer = createQuizRequest.expectedAnswer,
                level = createQuizRequest.level,
            )

            eventPublisher.publishEvent(
                NotApprovedQuizCreated.from(
                    notApprovedQuiz,
                    similarityQuizs.map { it.problem },
                )
            )
            return
        }

        val quizCreatePrompt = quizCreatePromptService.getFirstPrompt()
        require(aiApi.isDevelopmentQuiz(quizCreatePrompt.getRequestTextWithPrompt(text = createQuizRequest.problem))) {
            logger.warn("Only development quiz allow request: $createQuizRequest")
            "Only development quiz allow request: $createQuizRequest"
        }

        createQuizOrchestrator.sagaSync(
            request = createQuizRequest,
            context = mapOf(
                "userId" to user.id,
                "idempotencyKey" to IdGenerator.generate(),
                TRACE_ID to MDC.get(TRACE_ID),
            ),
        )
    }

    init {
        this.createQuizOrchestrator = orchestratorFactory
            .create<CreateQuizRequest>("create quiz orchestrator")
            .startWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, request ->
                    val userId = context.decodeContext("userId", Long::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)
                    identityApi.increaseUserPointsById(
                        userId = userId,
                        point = CREATE_QUIZ_PRICE,
                        idempotencyKey = idempotencyKey,
                    )

                    logger.info("Success give point to user. userId: $userId")

                    request
                },
                contextRollback = TraceIdContextRollback { context, _ ->
                    val userId = context.decodeContext("userId", Long::class)
                    val idempotencyKey = context.decodeContext("idempotencyKey", String::class)

                    identityApi.decreaseUserPointsById(
                        userId = userId,
                        point = CREATE_QUIZ_PRICE,
                        idempotencyKey = idempotencyKey,
                    )

                    logger.warn("Rollback give point to user. userId: $userId")
                }
            ).commitWithContext(
                contextOrchestrate = TraceIdContextOrchestrator { context, request ->
                    val userId = context.decodeContext("userId", Long::class)

                    quizService.createNewQuiz(
                        userId = userId,
                        problem = request.problem,
                        category = request.category,
                        expectedAnswer = request.expectedAnswer,
                        level = request.level,
                    )
                }
            )
    }

    companion object {
        private const val CREATE_QUIZ_PRICE = 5_000L
    }
}
