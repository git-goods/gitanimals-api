package org.gitanimals.notification.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.gitanimals.notification.app.SlackInteractRequestDispatcher
import org.gitanimals.notification.app.request.SlackInteractRequest
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SlackInteractController(
    private val objectMapper: ObjectMapper,
    private val slackInteractRequestDispatcher: SlackInteractRequestDispatcher,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    @PostMapping("/slack/interact", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun dispatchSlackInteracts(@RequestParam("payload") payload: String) {
        logger.info("payload: $payload")

        val slackRequest = objectMapper.readValue(payload, SlackInteractRequest::class.java)
        slackInteractRequestDispatcher.dispatch(slackRequest)
    }
}
