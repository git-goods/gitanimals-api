package org.gitanimals.quiz.domain.approved

import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language
import org.gitanimals.quiz.domain.core.Level
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val quizRepository: QuizRepository,
) {

    private var quizIdsAssociatedByLevelCache: Map<Level, List<Quiz>> = getQuizIdsAssociatedLevel()
    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    fun createNewQuiz(
        userId: Long,
        problem: String,
        level: Level,
        category: Category,
        expectedAnswer: String,
    ): Quiz {
        return quizRepository.save(
            Quiz.create(
                userId = userId,
                problem = problem,
                level = level,
                category = category,
                expectedAnswer = expectedAnswer,
            )
        )
    }

    fun findAllByIds(similarityQuizIds: List<Long>): List<Quiz> =
        quizRepository.findAllById(similarityQuizIds)

    fun findAllQuizByLevelAndCategoryAndLanguage(
        levels: List<Level>,
        category: Category,
        language: Language,
    ): List<Quiz> {
        val allQuizs: Map<Level, MutableList<Quiz>> = this.quizIdsAssociatedByLevelCache
            .mapValues { it.value.toMutableList() }

        checkAndCalibratePickableQuiz(
            levels = levels,
            category = category,
            language = language,
            allQuizs = allQuizs,
        )

        return runCatching {
            levels.map {
                val quiz = quizIdsAssociatedByLevelCache[it]?.random()
                    ?: throw IllegalStateException("Cannot pick quiz cause list is empty")
                allQuizs[it]?.remove(quiz)
                quiz
            }
        }.getOrElse {
            logger.error("Cannot call findAllQuizByLevel. levels: $levels", it)
            throw it
        }
    }

    private fun checkAndCalibratePickableQuiz(
        levels: List<Level>,
        category: Category,
        language: Language,
        allQuizs: Map<Level, MutableList<Quiz>>,
    ) {
        levels.forEach { level ->
            allQuizs[level]?.removeIf { it.category != category || it.language != language }

            val isPickable = allQuizs[level]
                ?.filter { it.category == category && it.language == language }
                ?.let { quizs ->
                    quizs.size >= levels.count { it == level }
                } ?: false

            check(isPickable) {
                val message =
                    "Fail to checkQuizPickable cause level: \"$level\" pickable size is smaller than request level size: \"${levels.count { it == level }}\""
                logger.warn(message)
                message
            }
        }
    }

    @Scheduled(cron = EVERY_1_HOURS)
    fun updateQuizCountCacheScheduled() {
        this.quizIdsAssociatedByLevelCache = getQuizIdsAssociatedLevel()
    }

    private fun getQuizIdsAssociatedLevel(): Map<Level, List<Quiz>> {
        val quizCountCache: MutableMap<Level, MutableList<Quiz>> =
            Level.entries.associateWith { mutableListOf<Quiz>() }.toMutableMap()

        var currentPage = 0
        val pageSize = 100
        var quizs = quizRepository.findAll(PageRequest.of(currentPage, pageSize))
        quizs.forEach { quizCountCache[it.level]?.add(it) }

        while (quizs.hasNext()) {
            currentPage += 1
            quizs = quizRepository.findAll(PageRequest.of(currentPage, pageSize))
            quizs.forEach { quizCountCache[it.level]?.add(it) }
        }

        return quizCountCache
    }

    companion object {
        private const val EVERY_1_HOURS = "0 0 * * * *"
    }
}
