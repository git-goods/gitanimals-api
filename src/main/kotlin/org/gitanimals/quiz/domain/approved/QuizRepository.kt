package org.gitanimals.quiz.domain.approved

import org.springframework.data.jpa.repository.JpaRepository

interface QuizRepository : JpaRepository<Quiz, Long>
