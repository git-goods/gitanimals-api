package org.gitanimals.quiz.app

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.gitanimals.quiz.app.request.CreateSolveQuizRequest
import org.gitanimals.quiz.domain.approved.QuizRepository
import org.gitanimals.quiz.domain.approved.QuizService
import org.gitanimals.quiz.domain.context.QuizSolveContextRepository
import org.gitanimals.quiz.domain.context.QuizSolveContextService
import org.gitanimals.quiz.domain.context.QuizSolveContextStatus
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
@DisplayName("SolveQuizFacadeTest 클래스의")
@TestPropertySource("classpath:test.properties")
internal class SolveQuizFacadeTest(
    private val solveQuizFacade: SolveQuizFacade,
    private val quizRepository: QuizRepository,
    private val quizSolveContextRepository: QuizSolveContextRepository,
    private val quizService: QuizService,
    private val quizSolveContextService: QuizSolveContextService,
    @MockkBean private val identityApi: IdentityApi,
) : DescribeSpec({

    afterAny {
        quizRepository.deleteAll()
        quizSolveContextRepository.deleteAll()
    }

    describe("createContext 메소드는") {
        context("token과 category를 입력받으면") {
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
                    solveQuizFacade.createContext(token,"KR" , request)
                }
            }
        }
    }

    describe("getAndStartSolveQuizContextById 메소드는") {
        context("token과 contextId에 해당하는 context의 상태가 NOT_STARTED혹은 SUCCESS라면") {
            val quizContext = quizSolveContextRepository.save(quizSolveContext(userId = 0L))
            every { identityApi.getUserByToken(any()) } returns defaultUser

            it("SOLVING상태의 QuizContextResponse를 응답한다") {
                val result = solveQuizFacade.getAndStartSolveQuizContextById(token, quizContext.id)

                result.should {
                    it.status shouldBe QuizSolveContextStatus.SOLVING
                    it.round.current shouldBe 1
                    it.round.total shouldBe 2
                    it.problem shouldBe "1"
                }
            }
        }
    }

    describe("answerQuizById 메소드는") {
        context("token과 contextId에 해당하는 context의 상태가 SOLVING이라면") {
            val quizContext =
                quizSolveContextRepository.save(quizSolveContext(userId = defaultUser.id.toLong()))
            quizSolveContextService.getAndStartSolveQuizContext(quizContext.id, quizContext.userId)
            every { identityApi.getUserByToken(any()) } returns defaultUser

            it("퀴즈를 푼다") {
                shouldNotThrowAny {
                    solveQuizFacade.answerQuizById(token, quizContext.id, "YES")
                }
            }
        }

        context("token과 contextId에 해당하는 context의 상태가 SOLVING이 아니라면") {
            val quizContext =
                quizSolveContextRepository.save(quizSolveContext(userId = defaultUser.id.toLong()))
            every { identityApi.getUserByToken(any()) } returns defaultUser

            it("IllegalArgumentException을 던진다") {
                shouldThrowExactly<IllegalArgumentException> {
                    solveQuizFacade.answerQuizById(token, quizContext.id, "YES")
                }
            }
        }
    }
}) {

    companion object {
        val token = "Bearer some token..."

        private val defaultUser = IdentityApi.UserResponse(
            id = "0",
            username = "devxb",
            points = "100000000",
        )
    }
}
