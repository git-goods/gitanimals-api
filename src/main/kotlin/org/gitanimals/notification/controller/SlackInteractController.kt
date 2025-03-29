package org.gitanimals.notification.controller

import org.gitanimals.notification.app.SlackInteractRequestDispatcher
import org.gitanimals.notification.app.request.SlackInteractRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SlackInteractController(
    private val slackInteractRequestDispatcher: SlackInteractRequestDispatcher,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    @PostMapping("/slack/interact")
    fun dispatchSlackInteracts(@RequestBody slackInteractRequest: SlackInteractRequest) {
        logger.info("SlackInteractRequest: $slackInteractRequest")
        slackInteractRequestDispatcher.dispatch(slackInteractRequest)
    }
}
