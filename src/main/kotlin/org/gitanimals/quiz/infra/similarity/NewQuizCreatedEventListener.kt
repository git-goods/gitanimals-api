package org.gitanimals.quiz.infra.similarity

import org.gitanimals.core.GracefulShutdownDispatcher.gracefulLaunch
import org.gitanimals.quiz.infra.event.NewQuizCreated
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NewQuizCreatedEventListener(
    private val tokenizer: Tokenizer,
    private val quizSimilarityRepository: QuizSimilarityRepository,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    @EventListener(NewQuizCreated::class)
    fun addQuizVectorToEs(newQuizCreated: NewQuizCreated) {
        gracefulLaunch {
            val tokenizedQuizText = runCatching {
                tokenizer.tokenize(Tokenizer.Request(newQuizCreated.problem))
            }.getOrElse {
                logger.error("Tokenize fail. id: ${newQuizCreated.id}, cause: ${it.message}", it)
                throw it
            }

            runCatching {
                val quizSimilarity = QuizSimilarity.from(newQuizCreated.id, tokenizedQuizText)

                quizSimilarityRepository.save(quizSimilarity)
            }.onSuccess {
                logger.info("Tokenize and save success. quizId: ${it.quizId}")
            }.onFailure {
                logger.error(
                    "Tokenize success but, Fail to save es. quizId: ${newQuizCreated.id}, cause: ${it.message}",
                    it
                )
            }
        }
    }
}
