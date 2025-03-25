package org.gitanimals.quiz.app

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import org.gitanimals.quiz.app.request.CreateSolveQuizRequest
import org.gitanimals.quiz.domain.approved.QuizRepository
import org.gitanimals.quiz.domain.approved.QuizService
import org.gitanimals.quiz.domain.context.QuizSolveContextService
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
@DisplayName("SolveQuizFacadeTest 클래스의")
@TestPropertySource("classpath:test.properties")
internal class SolveQuizFacadeTest(
    private val solveQuizFacade: SolveQuizFacade,
    private val quizRepository: QuizRepository,
    private val quizService: QuizService,
    @MockkBean private val identityApi: IdentityApi,
) : DescribeSpec({

    describe("createContext 메소드는") {
        context("token과 category를 입력받으면") {
            val token = "Bearer some token..."
            val request = CreateSolveQuizRequest(Category.BACKEND)
            quizRepository.saveAll(
                listOf(
                    quiz(level = Level.EASY),
                    quiz(level = Level.EASY),
                    quiz(level = Level.MEDIUM),
                    quiz(level = Level.DIFFICULT),
                    quiz(level = Level.DIFFICULT),
                )
            )
            quizService.updateQuizCountCacheScheduled()

            every { identityApi.getUserByToken(any()) } returns defaultUser

            it("새로운 quizContext를 생성한다") {
                shouldNotThrowAny {
                    solveQuizFacade.createContext(token, request)
                }
            }
        }
    }
}) {

    companion object {
        private val defaultUser = IdentityApi.UserResponse(
            id = "0",
            username = "devxb",
            points = "100000000",
        )
    }
}
