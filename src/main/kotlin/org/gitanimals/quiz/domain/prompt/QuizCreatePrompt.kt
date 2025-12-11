package org.gitanimals.quiz.domain.prompt

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "quiz_create_prompt")
class QuizCreatePrompt(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "text", columnDefinition = "VARCHAR(1000)")
    val text: String,
) {

    fun getRequestTextWithPrompt(text: String): String {
        return this.text + text
    }
}
