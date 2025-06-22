package org.gitanimals.quiz.domain.prompt.rag

import jakarta.persistence.*
import org.gitanimals.core.IdGenerator
import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language

@Entity
@Table(
    name = "quiz_create_rag",
    indexes = [
        Index(name = "quiz_idx_language", columnList = "language"),
        Index(name = "quiz_idx_category", columnList = "category"),
    ]
)
class QuizCreateRag(
    @Id
    @Column(name = "id")
    val id: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    val language: Language,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    val category: Category,

    @Column(name = "is_develop_quiz", nullable = false)
    val isDevelopQuiz: Boolean,

    @Column(name = "problem", length = 1000, columnDefinition = "VARCHAR(1000)", nullable = false)
    val problem: String,
) {

    companion object {
        fun create(language: Language, category: Category, isDevelopQuiz: Boolean, problem: String): QuizCreateRag {
            return QuizCreateRag(
                id = IdGenerator.generate(),
                language = language,
                category = category,
                isDevelopQuiz = isDevelopQuiz,
                problem = problem,
            )
        }
    }
}
