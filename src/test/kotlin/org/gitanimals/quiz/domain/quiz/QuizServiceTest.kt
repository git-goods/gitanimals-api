package org.gitanimals.quiz.domain.quiz

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gitanimals.quiz.domain.approved.QuizRepository
import org.gitanimals.quiz.domain.approved.QuizService
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language
import org.gitanimals.quiz.domain.core.Level
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@ContextConfiguration(classes = [QuizService::class])
@DisplayName("QuizService 클래스의")
@TestPropertySource("classpath:test.properties")
@EntityScan(basePackages = ["org.gitanimals.quiz.domain"])
@EnableJpaRepositories(basePackages = ["org.gitanimals.quiz.domain"])
internal class QuizServiceTest(
    private val quizService: QuizService,
    private val quizRepository: QuizRepository,
) : DescribeSpec({

    afterAny {
        quizRepository.deleteAll()
        quizService.updateQuizCountCacheScheduled()
    }

    describe("findAllQuizByLevel 메소드는") {
        context("뽑을 수 있는 퀴즈의 수가 인자로 받은 levels의 수 보다 작을 경우") {
            quizRepository.save(quiz(level = Level.EASY))
            quizRepository.save(quiz(level = Level.EASY))
            quizRepository.save(quiz(level = Level.MEDIUM))
            quizRepository.save(quiz(level = Level.DIFFICULT))
            quizService.updateQuizCountCacheScheduled()

            it("IllegalStateException을 던진다.") {
                val result = shouldThrowExactly<IllegalStateException> {
                    quizService.findAllQuizByLevelAndCategoryAndLanguage(
                        levels = listOf(
                            Level.EASY,
                            Level.EASY,
                            Level.MEDIUM,
                            Level.DIFFICULT,
                            Level.DIFFICULT,
                        ),
                        category = Category.BACKEND,
                        language = Language.KOREA,
                    )
                }

                result.message shouldContain "DIFFICULT"
            }
        }

        context("뽑을 수 있는 퀴즈의 수가 인자로 받은 levels의 수보다 큰 경우") {
            quizRepository.save(quiz(level = Level.EASY))
            quizRepository.save(quiz(level = Level.EASY))
            quizRepository.save(quiz(level = Level.MEDIUM))
            quizRepository.save(quiz(level = Level.DIFFICULT))
            quizRepository.save(quiz(level = Level.DIFFICULT))
            quizService.updateQuizCountCacheScheduled()

            it("levels에 해당하는 난이도를 가진 Quiz들을 반환한다") {
                val result = quizService.findAllQuizByLevelAndCategoryAndLanguage(
                    levels = listOf(
                        Level.EASY,
                        Level.EASY,
                        Level.MEDIUM,
                        Level.DIFFICULT,
                        Level.DIFFICULT,
                    ),
                    category = Category.BACKEND,
                    language = Language.KOREA,
                )

                result.size shouldBe 5
                result.filter { it.level == Level.EASY }.size shouldBe 2
                result.filter { it.level == Level.MEDIUM }.size shouldBe 1
                result.filter { it.level == Level.DIFFICULT }.size shouldBe 2
            }
        }

        context("뽑을 수 있는 퀴즈의 수가 인자로 받은 category로 필터링했을때 더 적어진다면") {
            quizRepository.save(quiz(level = Level.EASY))
            quizRepository.save(quiz(level = Level.EASY))
            quizRepository.save(quiz(level = Level.MEDIUM))
            quizRepository.save(quiz(level = Level.DIFFICULT))
            quizRepository.save(quiz(level = Level.DIFFICULT))
            quizService.updateQuizCountCacheScheduled()

            it("IllegalStateException을 던진다.") {
                val result = shouldThrowExactly<IllegalStateException> {
                    quizService.findAllQuizByLevelAndCategoryAndLanguage(
                        levels = listOf(
                            Level.EASY,
                            Level.EASY,
                            Level.MEDIUM,
                            Level.DIFFICULT,
                            Level.DIFFICULT,
                        ),
                        category = Category.FRONTEND,
                        language = Language.KOREA,
                    )
                }

                result.message shouldContain "EASY"
            }
        }

        context("뽑을 수 있는 퀴즈의 수가 인자로 받은 language로 필터링했을때 더 적어진다면") {
            quizRepository.save(quiz(level = Level.EASY))
            quizRepository.save(quiz(level = Level.EASY))
            quizRepository.save(quiz(level = Level.MEDIUM))
            quizRepository.save(quiz(level = Level.DIFFICULT))
            quizRepository.save(quiz(level = Level.DIFFICULT))
            quizService.updateQuizCountCacheScheduled()

            it("IllegalStateException을 던진다.") {
                val result = shouldThrowExactly<IllegalStateException> {
                    quizService.findAllQuizByLevelAndCategoryAndLanguage(
                        levels = listOf(
                            Level.EASY,
                            Level.EASY,
                            Level.MEDIUM,
                            Level.DIFFICULT,
                            Level.DIFFICULT,
                        ),
                        category = Category.FRONTEND,
                        language = Language.ENGLISH,
                    )
                }

                result.message shouldContain "EASY"
            }
        }
    }
})
