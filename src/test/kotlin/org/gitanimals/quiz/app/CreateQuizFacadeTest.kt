package org.gitanimals.quiz.app

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import org.gitanimals.core.IdGenerator
import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.gitanimals.quiz.app.request.CreateQuizRequest
import org.gitanimals.quiz.domain.Category
import org.gitanimals.quiz.domain.Level
import org.gitanimals.quiz.domain.QuizService
import org.gitanimals.quiz.infra.HibernateEventListenerConfiguration
import org.gitanimals.quiz.infra.NewQuizCreatedInsertEventListener
import org.gitanimals.quiz.infra.event.NewQuizCreated
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.time.Duration.Companion.seconds

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ContextConfiguration(
    classes = [
        RedisContainer::class,
        QuizService::class,
        CreateQuizFacade::class,
        DomainEventHolder::class,
        NewQuizCreatedInsertEventListener::class,
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
    @MockkBean private val identityApi: IdentityApi,
) : DescribeSpec({

    extension(SpringExtension)

    beforeEach {
        MDC.put(TRACE_ID, IdGenerator.generate().toString())
        every { identityApi.getUserByToken(any()) } returns defaultUser
        domainEventHolder.deleteAll()
    }

    describe("createQuiz 메소드는") {
        context("token과 createQuizRequest를 받아서") {
            it("새로운 퀴즈를 생성하고, 퀴즈 생성 이벤트를 발행한다.") {
                createQuizFacade.createQuiz(token, createQuizRequest)

                eventually(5.seconds) {
                    domainEventHolder.eventsShouldBe(NewQuizCreated::class, 1)
                }
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
