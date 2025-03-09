package org.gitanimals.quiz.domain

import jakarta.persistence.*
import org.gitanimals.core.IdGenerator

@Entity
@Table(name = "quiz")
class Quiz(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id", nullable = false, unique = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "level", columnDefinition = "VARCHAR(50)", nullable = false)
    val level: Level,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", columnDefinition = "TEXT", nullable = false)
    val category: Category,

    @Column(name = "problem", length = 1000, columnDefinition = "VARCHAR(1000)", nullable = false)
    val problem: String,

    @Column(name = "expected_answer", columnDefinition = "TEXT", nullable = false)
    val expectedAnswer: String,

    @Column(name = "approval", nullable = false)
    val approval: Boolean,
) {

    companion object {

        fun create(
            userId: Long,
            level: Level,
            problem: String,
            category: Category,
            expectedAnswer: String,
        ): Quiz {
            return Quiz(
                id = IdGenerator.generate(),
                userId = userId,
                level = level,
                problem = problem,
                category = category,
                expectedAnswer = expectedAnswer,
                approval = false,
            )
        }
    }
}
