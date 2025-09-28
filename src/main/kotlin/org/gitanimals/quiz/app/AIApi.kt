package org.gitanimals.quiz.app

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.PostExchange

@Component
class AIApi(
    private val openAI: OpenAI,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    fun isDevelopmentQuiz(prompt: String, text: String): Boolean {
        val request = OpenAI.Request(
            instructions = prompt,
            input = text,
        )

        logger.info("[AIApi] request: $request")
        val aiResponseContent = openAI.invoke(request)
            .also {
                logger.info("[AIApi] response: $it")
            }
            .output
            .first { it.type == "message" }
            .content[0].text

        return when (aiResponseContent.trim().lowercase()) {
            "true" -> true
            "false" -> false
            else -> error("Cannot parsing content cause content is not \"true\" or \"false\". content: \"${aiResponseContent.trim()}\"")
        }
    }
}

fun interface OpenAI {

    @PostExchange("/v1/responses")
    fun invoke(@RequestBody request: Request): Response

    data class Request(
        val model: String = "gpt-5-nano",
        val instructions: String,
        val input: String,
    ) {

        data class Message(
            val role: String,
            val content: String,
        )
    }

    data class Response(
        val id: String,
        val model: String,
        val output: List<Output>,
    ) {
        data class Output(
            val id: String,
            val type: String,
            val content: List<Content>,
        ) {

            data class Content(
                val type: String,
                val text: String,
            )
        }
    }
}
