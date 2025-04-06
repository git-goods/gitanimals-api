package org.gitanimals.notification.infra

import org.gitanimals.core.redis.RedisPubSubChannel
import org.gitanimals.notification.app.NotApprovedQuizCreatedMessageListener
import org.gitanimals.notification.app.QuizCreatedMessageListener
import org.gitanimals.notification.app.SlackDeadLetterMessageListener
import org.gitanimals.notification.app.SlackRepliedMessageListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer

@Configuration
class RedisMessageListenerConfiguration(
    private val redisConnectionFactory: RedisConnectionFactory,
    private val quizCreatedMessageListener: QuizCreatedMessageListener,
    private val notApprovedQuizCreatedMessageListener: NotApprovedQuizCreatedMessageListener,
    private val slackRepliedMessageListener: SlackRepliedMessageListener,
    private val slackDeadLetterMessageListener: SlackDeadLetterMessageListener,
) {

    @Bean
    fun notificationRedisListenerContainer(): RedisMessageListenerContainer {
        return RedisMessageListenerContainer().apply {
            this.connectionFactory = redisConnectionFactory
            this.addMessageListener(
                quizCreatedMessageListener,
                ChannelTopic(RedisPubSubChannel.NEW_QUIZ_CREATED),
            )
            this.addMessageListener(
                notApprovedQuizCreatedMessageListener,
                ChannelTopic(RedisPubSubChannel.NOT_APPROVED_QUIZ_CREATED),
            )
            this.addMessageListener(
                slackRepliedMessageListener,
                ChannelTopic(RedisPubSubChannel.SLACK_REPLIED),
            )
            this.addMessageListener(
                slackDeadLetterMessageListener,
                ChannelTopic(RedisPubSubChannel.DEAD_LETTER_OCCURRED),
            )
        }
    }
}
