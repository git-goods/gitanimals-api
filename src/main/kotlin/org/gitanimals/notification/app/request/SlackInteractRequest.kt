package org.gitanimals.notification.app.request

import com.fasterxml.jackson.annotation.JsonProperty

data class SlackInteractRequest(
    val user: SlackInteractUser,
    val container: Container,
    val actions: List<Action>,
) {

    data class SlackInteractUser(
        val id: String,
        val username: String,
        @JsonProperty("team_id")
        val teamId: String,
    )

    data class Container(
        val type: String,
        @JsonProperty("message_ts")
        val messageTs: String,
        @JsonProperty("attachment_id")
        val attachmentId: String,
        @JsonProperty("channel_id")
        val channelId: String,
    )

    data class Action(
        @JsonProperty("action_id")
        val actionId: String,
        val value: String,
    )
}
