package org.gitanimals.quiz.domain.prompt.rag

import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language
import org.springframework.stereotype.Service

@Service
class QuizCreateRagService(
    private val quizCreateRagRepository: QuizCreateRagRepository,
) {

    fun findAllByLanguageAndCategory(language: Language, category: Category): List<QuizCreateRag> {
        return quizCreateRagRepository.findAllByLanguageAndCategory(
            language = language,
            category = category,
        )
    }

    fun createQuizCreateRag(
        language: Language,
        category: Category,
        isDevelopQuiz: Boolean,
        problem: String,
    ): QuizCreateRag {
        return quizCreateRagRepository.save(
            QuizCreateRag.create(
                language = language,
                category = category,
                isDevelopQuiz = isDevelopQuiz,
                problem = problem,
            )
        )
    }
}
