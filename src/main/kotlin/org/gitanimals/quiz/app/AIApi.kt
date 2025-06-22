package org.gitanimals.quiz.app

import org.gitanimals.quiz.app.OpenAI.Request.Message
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.PostExchange

@Component
class AIApi(
    private val openAI: OpenAI,
) {

    fun isDevelopmentQuiz(prompt: String, text: String): Boolean {
        val request = OpenAI.Request(
            messages = listOf(
                Message(role = "system", content = prompt),
                Message(role = "user", content = text)
            )
        )
        val aiResponseContent = openAI.invoke(request).choices.first().message.content
        return when (aiResponseContent.trim().lowercase()) {
            "true" -> true
            "false" -> false
            else -> error("Cannot parsing content cause content is not \"true\" or \"false\". content: \"${aiResponseContent.trim()}\"")
        }
    }
}

fun interface OpenAI {

    @PostExchange("/v1/chat/completions")
    fun invoke(@RequestBody request: Request): Response

    data class Request(
        val model: String = "o4-mini",
        val messages: List<Message>,
    ) {

        data class Message(
            val role: String,
            val content: String,
        )
    }

    data class Response(
        val id: String,
        val `object`: String,
        val created: Long,
        val model: String,
        val choices: List<Choice>,
        val usage: Usage,
        val service_tier: String,
        val system_fingerprint: String
    ) {
        data class Choice(
            val index: Int,
            val message: Message,
            val logprobs: String?,
            val finish_reason: String
        ) {

            data class Message(
                val role: String,
                val content: String,
                val refusal: String?,
                val annotations: List<String>
            ) {

            }
        }

        data class Usage(
            val prompt_tokens: Int,
            val completion_tokens: Int,
            val total_tokens: Int,
            val prompt_tokens_details: TokenDetails,
            val completion_tokens_details: TokenDetails
        ) {
            data class TokenDetails(
                val cached_tokens: Int,
                val audio_tokens: Int
            )
        }
    }
}
