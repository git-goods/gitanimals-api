package org.gitanimals.quiz.domain.context

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.verify
import org.gitanimals.quiz.app.DomainEventHolder
import org.gitanimals.quiz.app.IdentityApi
import org.gitanimals.quiz.app.InboxApi
import org.gitanimals.quiz.domain.quiz.quiz
import org.gitanimals.quiz.infra.hibernate.*
import org.gitanimals.quiz.infra.hibernate.QuizSolveContextDoneLogicDelegator.QuizSolveContextDone
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.time.Duration.Companion.seconds

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ContextConfiguration(
    classes = [
        DomainEventHolder::class,
        QuizDeletedHibernateEventListener::class,
        HibernateEventListenerConfiguration::class,
        QuizSolveContextDoneHibernateEventListener::class,
        NewQuizCreatedInsertHibernateEventListener::class,
        QuizSolveContextDoneLogicDelegator::class,
        QuizSolveContextService::class,
    ]
)
@EntityScan(basePackages = ["org.gitanimals.quiz.domain"])
@EnableJpaRepositories(basePackages = ["org.gitanimals.quiz.domain"])
@TestPropertySource("classpath:test.properties")
@DisplayName("QuizSolveContextService 클래스의")
class QuizSolveContextServiceTest(
    private val quizSolveContextService: QuizSolveContextService,
    private val quizSolveContextRepository: QuizSolveContextRepository,
    private val domainEventHolder: DomainEventHolder,
    @MockkBean(relaxed = true) private val inboxApi: InboxApi,
    @MockkBean(relaxed = true) private val identityApi: IdentityApi,
) : StringSpec({

    "solveQuiz 메소드는 퀴즈가 DONE으로 변경된 경우 유저에게 포인트를 지급한다" {
        // given
        val userId = 1L
        val quiz = quiz()
        val quizSolveContext: QuizSolveContext = quizSolveContext(userId = userId, quizs = listOf(quiz))
        quizSolveContextRepository.save(quizSolveContext).id

        quizSolveContextService.getAndStartSolveQuizContext(
            id = quizSolveContext.id,
            userId = userId,
        )

        // when
        quizSolveContextService.solveQuiz(
            id = quizSolveContext.id,
            userId = userId,
            answer = "YES",
        )

        val result = quizSolveContextRepository.findByIdOrNull(quizSolveContext.id)!!
        // then

        result.getStatus() shouldBe QuizSolveContextStatus.DONE
        eventually(5.seconds) {
            domainEventHolder.eventsShouldBe(QuizSolveContextDone::class, 1)
        }
        verify(exactly = 1) {
            identityApi.increaseUserPointsById(any(), any(), any())
        }
    }
}) {

}
