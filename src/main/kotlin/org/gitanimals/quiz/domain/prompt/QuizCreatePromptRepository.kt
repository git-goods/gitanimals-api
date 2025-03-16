package org.gitanimals.quiz.domain.prompt

import org.springframework.data.jpa.repository.JpaRepository

interface QuizCreatePromptRepository : JpaRepository<QuizCreatePrompt, Long>
