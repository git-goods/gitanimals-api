package org.gitanimals.quiz.infra

import org.gitanimals.core.redis.RedisPubSubChannel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer

@Configuration
class QuizRedisMessageListenerConfiguration(
    private val redisConnectionFactory: RedisConnectionFactory,
    private val notApprovedQuizSlackInteractedMessageListener: NotApprovedQuizSlackInteractedMessageListener,
    private val quizDeleteSlackInteractedMessageListener: QuizDeleteSlackInteractedMessageListener,
) {

    @Bean
    fun quizRedisListenerContainer(): RedisMessageListenerContainer {
        return RedisMessageListenerContainer().apply {
            this.connectionFactory = redisConnectionFactory
            this.addMessageListener(
                notApprovedQuizSlackInteractedMessageListener,
                ChannelTopic(RedisPubSubChannel.SLACK_INTERACTED),
            )
            this.addMessageListener(
                quizDeleteSlackInteractedMessageListener,
                ChannelTopic(RedisPubSubChannel.SLACK_INTERACTED),
            )
        }
    }
}
