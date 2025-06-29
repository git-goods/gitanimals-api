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
                tokenizer.embed(Tokenizer.Request.from(newQuizCreated.problem))
            }.onSuccess {
                logger.info("[NewQuizCreatedEventListener] Embedding Success total token: ${it.usage.totalToken}, prompt token: ${it.usage.promptToken}")
            }.getOrElse {
                logger.error(
                    "[NewQuizCreatedEventListener] Tokenize fail. id: ${newQuizCreated.id}, cause: ${it.message}",
                    it
                )
                throw it
            }.data.embedding

            runCatching {
                val quizSimilarity = QuizSimilarity.from(newQuizCreated.id, tokenizedQuizText)
                quizSimilarityRepository.save(quizSimilarity)
            }.onSuccess {
                logger.info("[NewQuizCreatedEventListener] Tokenize and save success. quizId: ${it.quizId}")
            }.onFailure {
                logger.error(
                    "[NewQuizCreatedEventListener] Tokenize success but, Fail to save es. quizId: ${newQuizCreated.id}, cause: ${it.message}",
                    it
                )
            }
        }
    }
}
