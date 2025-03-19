package org.gitanimals.quiz.domain

import org.springframework.data.jpa.repository.JpaRepository

interface NotApprovedQuizRepository : JpaRepository<NotApprovedQuiz, Long> {

    fun findAllByUserId(userId: Long): List<NotApprovedQuiz>
}
