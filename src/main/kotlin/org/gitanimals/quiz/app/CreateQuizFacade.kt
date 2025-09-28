package org.gitanimals.quiz.app

import org.gitanimals.core.IdGenerator
import org.gitanimals.core.TraceIdContextOrchestrator
import org.gitanimals.core.TraceIdContextRollback
import org.gitanimals.core.auth.InternalAuth
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ENTRY_POINT
import org.gitanimals.core.filter.MDCFilter.Companion.USER_ID
import org.gitanimals.quiz.app.event.NotApprovedQuizCreated
import org.gitanimals.quiz.app.event.NotDeveloperQuizCreateRequested
import org.gitanimals.quiz.app.request.CreateQuizRequest
import org.gitanimals.quiz.app.response.CreateQuizResponse
import org.gitanimals.quiz.domain.approved.QuizService
import org.gitanimals.quiz.domain.core.Language
import org.gitanimals.quiz.domain.core.Language.Companion.containsKorean
import org.gitanimals.quiz.domain.not_approved.NotApprovedQuizService
import org.gitanimals.quiz.domain.prompt.QuizCreatePromptService
import org.gitanimals.quiz.domain.prompt.rag.QuizCreateRag
import org.gitanimals.quiz.domain.prompt.rag.QuizCreateRagService
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
    private val internalAuth: InternalAuth,
    private val quizService: QuizService,
    private val notApprovedQuizService: NotApprovedQuizService,
    private val textSimilarityChecker: TextSimilarityChecker,
    private val eventPublisher: ApplicationEventPublisher,
    private val quizCreatePromptService: QuizCreatePromptService,
    private val quizCreateRagService: QuizCreateRagService,
    orchestratorFactory: OrchestratorFactory,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    private lateinit var createQuizOrchestrator: Orchestrator<CreateQuizRequest, CreateQuizResponse>

    fun createQuiz(createQuizRequest: CreateQuizRequest): CreateQuizResponse {
        val userId = internalAuth.getUserId()

        val quizCreatePrompt = quizCreatePromptService.getFirstPrompt()
        val language = when {
            createQuizRequest.problem.containsKorean() -> Language.KOREA
            else -> Language.ENGLISH
        }
        val quizCreateRags = quizCreateRagService.findAllByLanguageAndCategory(
            language = language,
            category = createQuizRequest.category,
        )

        val isDevelopmentQuiz = runCatching {
            aiApi.isDevelopmentQuiz(
                prompt = quizCreateRags.toPrompt(),
                text = quizCreatePrompt.getRequestTextWithPrompt(text = createQuizRequest.problem)
            )
        }.getOrElse {
            logger.error("Validation fail on isDevelopmentQuiz cause ${it.message}", it)
            throw it
        }

        require(isDevelopmentQuiz) {
            logger.warn("Only development quiz allow request: $createQuizRequest")
            eventPublisher.publishEvent(
                NotDeveloperQuizCreateRequested(
                    category = createQuizRequest.category,
                    problem = createQuizRequest.problem,
                    language = language,
                )
            )
            "Only development quiz allow request: $createQuizRequest"
        }

        val similarityResponses = textSimilarityChecker.getSimilarity(createQuizRequest.problem)
        if (similarityResponses.similarityQuizIds.isNotEmpty()) {
            logger.info("Cannot pass similarity check, need to human intelligence.")

            val similarityQuizs = quizService.findAllByIds(similarityResponses.similarityQuizIds)

            val notApprovedQuiz = notApprovedQuizService.createNotApprovedQuiz(
                userId = userId,
                problem = createQuizRequest.problem,
                category = createQuizRequest.category,
                expectedAnswer = createQuizRequest.expectedAnswer,
                level = createQuizRequest.level,
            )

            identityApi.increaseUserPointsById(
                userId = userId,
                point = CREATE_QUIZ_PRICE,
                idempotencyKey = IdGenerator.generate().toString(),
            )

            eventPublisher.publishEvent(
                NotApprovedQuizCreated.from(
                    notApprovedQuiz,
                    similarityQuizs.map { it.problem },
                )
            )

            return CreateQuizResponse.underReview(
                point = CREATE_QUIZ_PRICE,
                message = CREATE_QUIZ_SIMILARITY_CHECK_MESSAGE
            )
        }

        return createQuizOrchestrator.sagaSync(
            request = createQuizRequest,
            context = mapOf(
                "userId" to userId,
                "idempotencyKey" to IdGenerator.generate(),
                TRACE_ID to MDC.get(TRACE_ID),
                USER_ID to MDC.get(USER_ID),
                USER_ENTRY_POINT to MDC.get(USER_ENTRY_POINT),
            ),
        ).decodeResultOrThrow(CreateQuizResponse::class)
    }

    private fun List<QuizCreateRag>.toPrompt(): String {
        val quizCreateRag = this.map {
            "problem: ${it.problem}. is developer quiz?: ${it.isDevelopQuiz}"
        }.joinToString { "\n" }

        return """
            Here are the quiz questions that have been identified as development-related so far.
            Please refer to these when determining whether a quiz is related to development.
            
            $quizCreateRag
        """.trimIndent()
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

                    quizService.updateQuizCountCacheScheduled()

                    CreateQuizResponse.success(
                        point = CREATE_QUIZ_PRICE,
                        message = CREATE_QUIZ_SUCCESS_MESSAGE
                    )
                }
            )
    }

    companion object {
        const val CREATE_QUIZ_PRICE = 5_000L
        private const val CREATE_QUIZ_SIMILARITY_CHECK_MESSAGE =
            "Your quiz has been successfully created, but a similar quiz has been found and is under review. The awarded $CREATE_QUIZ_PRICE points may be revoked."
        private const val CREATE_QUIZ_SUCCESS_MESSAGE =
            "Success to create quiz and get $CREATE_QUIZ_PRICE points"
    }
}
