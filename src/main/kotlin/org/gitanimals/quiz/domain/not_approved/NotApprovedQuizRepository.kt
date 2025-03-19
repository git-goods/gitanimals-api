package org.gitanimals.quiz.domain.not_approved

import org.springframework.data.jpa.repository.JpaRepository

interface NotApprovedQuizRepository : JpaRepository<NotApprovedQuiz, Long> {

    fun findAllByUserId(userId: Long): List<NotApprovedQuiz>
}
