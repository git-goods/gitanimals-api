package org.gitanimals.quiz.domain.prompt

import org.springframework.stereotype.Service

@Service
class QuizCreatePromptService(
    private val quizCreatePromptRepository: QuizCreatePromptRepository,
) {

    fun getFirstPrompt(): QuizCreatePrompt = quizCreatePromptRepository.findAll().first()
}
