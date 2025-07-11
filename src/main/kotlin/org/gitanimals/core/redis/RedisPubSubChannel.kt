package org.gitanimals.core.redis

object RedisPubSubChannel {

    const val NEW_QUIZ_CREATED = "NEW_QUIZ_CREATED"
    const val NOT_APPROVED_QUIZ_CREATED = "NOT_APPROVED_QUIZ_CREATED"
    const val NOT_DEVELOPER_QUIZ_CREATED = "NOT_DEVELOPER_QUIZ_CREATED"

    const val SLACK_INTERACTED = "SLACK_INTERACTED"
    const val SLACK_REPLIED = "SLACK_REPLIED"

    const val DEAD_LETTER_OCCURRED = "DEAD_LETTER_OCCURRED"

    const val NEW_PET_DROP_RATE_DISTRIBUTION = "NEW_PET_DROP_RATE_DISTRIBUTION"
}
