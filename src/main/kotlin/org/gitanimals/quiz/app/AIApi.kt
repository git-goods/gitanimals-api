package org.gitanimals.quiz.app

import org.gitanimals.quiz.app.AIApi.Request.Message
import org.springframework.web.bind.annotation.GetMapping

fun interface AIApi {

    fun isDevelopmentQuiz(text: String): Boolean {
        val request = Request(messages = listOf(Message(role = "user", content = text)))
        return invoke(request)
    }

    @GetMapping("/v1/chat/completions")
    fun invoke(request: Request): Boolean

    data class Request(
        val model: String = "gpt-4o",
        val messages: List<Message>,
    ) {

        data class Message(
            val role: String,
            val content: String,
        )
    }
}
