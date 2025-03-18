package org.gitanimals.quiz.app

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.quiz.app.event.NotApprovedQuizCreated
import org.gitanimals.quiz.app.request.CreateQuizRequest
import org.gitanimals.quiz.domain.*
import org.gitanimals.quiz.domain.prompt.QuizCreatePrompt
import org.gitanimals.quiz.domain.prompt.QuizCreatePromptRepository
import org.gitanimals.quiz.domain.prompt.QuizCreatePromptService
import org.gitanimals.quiz.infra.HibernateEventListenerConfiguration
import org.gitanimals.quiz.infra.NewQuizCreatedInsertHibernateEventListener
import org.gitanimals.quiz.infra.event.NewQuizCreated
import org.rooftop.netx.meta.EnableSaga
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.time.Duration.Companion.seconds

@EnableSaga
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ContextConfiguration(
    classes = [
        RedisContainer::class,
        QuizService::class,
        CreateQuizFacade::class,
        DomainEventHolder::class,
        NotApprovedQuizService::class,
        QuizCreatePromptService::class,
        NewQuizCreatedInsertHibernateEventListener::class,
        HibernateEventListenerConfiguration::class,
    ]
)
@EntityScan(basePackages = ["org.gitanimals.quiz.domain"])
@EnableJpaRepositories(basePackages = ["org.gitanimals.quiz.domain"])
@DisplayName("CreateQuizFacade 클래스의")
@TestPropertySource("classpath:test.properties")
internal class CreateQuizFacadeTest(
    private val createQuizFacade: CreateQuizFacade,
    private val domainEventHolder: DomainEventHolder,
    private val quizRepository: QuizRepository,
    private val notApprovedQuizRepository: NotApprovedQuizRepository,
    private val quizCreatePromptRepository: QuizCreatePromptRepository,
    @MockkBean private val identityApi: IdentityApi,
    @MockkBean private val textSimilarityChecker: TextSimilarityChecker,
    @MockkBean private val aiApi: AIApi,
) : DescribeSpec({

    extension(SpringExtension)

    beforeEach {
        quizCreatePromptRepository.deleteAll()
        quizCreatePromptRepository.save(QuizCreatePrompt(1L, "Hello AI"))
        MDC.put(TRACE_ID, IdGenerator.generate().toString())
        every { identityApi.getUserByToken(any()) } returns defaultUser
        every { identityApi.increaseUserPointsById(any(), any(), any()) } just Runs
        every { identityApi.decreaseUserPointsById(any(), any(), any()) } just Runs
        every { aiApi.isDevelopmentQuiz(any()) } returns true
        domainEventHolder.deleteAll()
    }

    describe("createQuiz 메소드는") {
        context("token과 createQuizRequest를 받아서") {
            every { textSimilarityChecker.getSimilarity(any()) } returns SimilarityResponse(
                emptyList()
            )

            it("새로운 퀴즈를 생성하고, 퀴즈 생성 이벤트를 발행한다.") {
                createQuizFacade.createQuiz(token, createQuizRequest)

                eventually(5.seconds) {
                    domainEventHolder.eventsShouldBe(NewQuizCreated::class, 1)
                }
            }
        }

        context("유사도 검색에 1개이상의 유사한 퀴즈가 발견되면") {
            val quiz = quizRepository.save(quiz())
            every { textSimilarityChecker.getSimilarity(any()) } returns SimilarityResponse(
                listOf(quiz.id)
            )

            it("NotApprovedQuiz를 생성하고, NotApprovedQuizCreated 이벤트를 발행한다.") {
                createQuizFacade.createQuiz(token, createQuizRequest)

                eventually(5.seconds) {
                    domainEventHolder.eventsShouldBe(NotApprovedQuizCreated::class, 1)
                }
                notApprovedQuizRepository.findAll().size shouldBe 1
            }
        }
    }
}) {

    companion object {
        val token = "Bearer sehrnsafbhsfbasdfjhsabfjasdbfhafjasdfshfbsa"

        private val defaultUser = IdentityApi.UserResponse(
            id = "1",
            username = "devxb",
            points = "100000"
        )

        private val createQuizRequest = CreateQuizRequest(
            level = Level.MEDIUM,
            category = Category.BACKEND,
            problem = "테스트",
            expectedAnswer = "YES",
        )
    }
}
