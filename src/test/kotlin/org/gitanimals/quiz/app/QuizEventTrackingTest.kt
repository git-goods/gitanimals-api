package org.gitanimals.quiz.app

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.gitanimals.core.auth.InternalAuth
import org.gitanimals.core.event.EventLogger
import org.gitanimals.quiz.app.request.CreateSolveQuizRequest
import org.gitanimals.quiz.domain.approved.QuizRepository
import org.gitanimals.quiz.domain.approved.QuizService
import org.gitanimals.quiz.domain.context.QuizSolveContextRepository
import org.gitanimals.quiz.domain.context.QuizSolveContextService
import org.gitanimals.quiz.domain.context.quizSolveContext
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Level
import org.gitanimals.quiz.domain.quiz.quiz
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@ContextConfiguration(
    classes = [
        SolveQuizFacade::class,
        QuizSolveContextService::class,
        QuizService::class,
    ]
)
@EntityScan(basePackages = ["org.gitanimals.quiz.domain"])
@EnableJpaRepositories(basePackages = ["org.gitanimals.quiz.domain"])
@DisplayName("QuizEventTrackingTest 클래스의")
@TestPropertySource("classpath:test.properties")
internal class QuizEventTrackingTest(
    private val solveQuizFacade: SolveQuizFacade,
    private val quizRepository: QuizRepository,
    private val quizSolveContextRepository: QuizSolveContextRepository,
    private val quizSolveContextService: QuizSolveContextService,
    @MockkBean private val internalAuth: InternalAuth,
    @MockkBean private val identityApi: IdentityApi,
    @MockkBean private val eventLogger: EventLogger,
) : DescribeSpec({

    beforeAny {
        every { internalAuth.getUserId() } returns userId
        every { eventLogger.track(any(), any(), any()) } just runs
    }

    afterAny {
        quizRepository.deleteAll()
        quizSolveContextRepository.deleteAll()
    }

    describe("answerQuizById 메소드는") {
        context("퀴즈 답변 제출 후") {
            val quizContext = quizSolveContextRepository.save(quizSolveContext(userId = userId))
            quizSolveContextService.getAndStartSolveQuizContext(quizContext.id, quizContext.userId)

            it("submit_quiz_answer 이벤트를 트래킹한다") {
                solveQuizFacade.answerQuizById(quizContext.id, "YES")

                verify(exactly = 1) {
                    eventLogger.track(
                        eventName = "submit_quiz_answer",
                        distinctId = userId.toString(),
                        properties = match { props ->
                            props["quiz_id"] == quizContext.id &&
                                props["user_id"] == userId
                        }
                    )
                }
            }
        }

    }

    describe("createContext 메소드는") {
        context("퀴즈 컨텍스트 생성 시") {
            quizRepository.saveAll(
                listOf(
                    quiz(level = Level.EASY),
                    quiz(level = Level.EASY),
                    quiz(level = Level.MEDIUM),
                    quiz(level = Level.DIFFICULT),
                    quiz(level = Level.DIFFICULT),
                )
            )
            every { identityApi.getUserByToken(any()) } returns defaultUser

            it("createContext가 정상 동작한다") {
                solveQuizFacade.createContext("KR", CreateSolveQuizRequest(Category.BACKEND))
            }
        }
    }
}) {

    companion object {
        private const val userId = 42L

        private val defaultUser = IdentityApi.UserResponse(
            id = userId.toString(),
            username = "testuser",
            points = "1000",
        )
    }
}
