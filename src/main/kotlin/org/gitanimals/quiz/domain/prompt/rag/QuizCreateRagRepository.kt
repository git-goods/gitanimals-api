package org.gitanimals.quiz.domain.prompt.rag

import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language
import org.springframework.data.jpa.repository.JpaRepository

interface QuizCreateRagRepository : JpaRepository<QuizCreateRag, Long> {

    fun findAllByLanguageAndCategory(language: Language, category: Category): List<QuizCreateRag>
}
