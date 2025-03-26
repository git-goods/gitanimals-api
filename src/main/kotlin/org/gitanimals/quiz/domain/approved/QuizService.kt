package org.gitanimals.quiz.domain.approved

import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Level
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val quizRepository: QuizRepository,
) {

    private var quizIdsAssociatedByLevelCache: Map<Level, List<Long>> = getQuizIdsAssociatedLevel()
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

    fun findAllQuizByLevel(levels: List<Level>): List<Quiz> {
        val allQuizs: Map<Level, MutableList<Long>> = this.quizIdsAssociatedByLevelCache
            .mapValues { it.value.toMutableList() }

        checkQuizPickable(levels, allQuizs)

        return runCatching {
            val quizIds = levels.map {
                val quizId = quizIdsAssociatedByLevelCache[it]?.random()
                allQuizs[it]?.remove(quizId)
                quizId
            }

            quizRepository.findAllById(quizIds)
        }.getOrElse {
            logger.error("Cannot call findAllQuizByLevel. levels: $levels", it)
            throw it
        }
    }

    private fun checkQuizPickable(levels: List<Level>, allQuizs: Map<Level, MutableList<Long>>) {
        levels.forEach { level ->
            val isPickable = allQuizs[level]?.let { quizIds ->
                quizIds.size >= levels.count { it == level }
            } ?: false

            check(isPickable) {
                val message = "Fail to checkQuizPickable cause level: \"$level\" pickable size is smaller than request level size: \"${levels.count { it == level }}\""
                logger.warn(message)
                message
            }
        }
    }

    @Scheduled(cron = EVERY_1_HOURS)
    fun updateQuizCountCacheScheduled() {
        this.quizIdsAssociatedByLevelCache = getQuizIdsAssociatedLevel()
    }

    private fun getQuizIdsAssociatedLevel(): Map<Level, List<Long>> {
        val quizCountCache: MutableMap<Level, MutableList<Long>> =
            Level.entries.associateWith { mutableListOf<Long>() }.toMutableMap()

        var currentPage = 0
        val pageSize = 100
        var quizs = quizRepository.findAll(PageRequest.of(currentPage, pageSize))
        quizs.forEach { quizCountCache[it.level]?.add(it.id) }

        while (quizs.hasNext()) {
            currentPage += 1
            quizs = quizRepository.findAll(PageRequest.of(currentPage, pageSize))
            quizs.forEach { quizCountCache[it.level]?.add(it.id) }
        }

        return quizCountCache
    }

    companion object {
        private const val EVERY_1_HOURS = "0 0 * * * *"
    }
}
