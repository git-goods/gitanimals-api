package org.gitanimals.identity.app

import org.gitanimals.core.AUTHORIZATION_EXCEPTION
import org.gitanimals.identity.domain.UserService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.RedisStringCommands
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.types.Expiration
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@Component
class RefreshTokenFacade(
    private val userService: UserService,
    private val tokenManager: TokenManager,
    @Value("\${login.secret}") private val loginSecret: String,
    @Qualifier("gitanimalsRedisTemplate") private val redisTemplate: StringRedisTemplate,
) {

    fun getTokenByRefreshToken(loginSecret: String, refreshToken: String): Token {
        if (loginSecret != this.loginSecret) {
            throw AUTHORIZATION_EXCEPTION
        }

        val userId = redisTemplate.execute { conn ->
            val userId: String = conn.stringCommands().get(refreshToken.toByteArray())?.let {
                String(it)
            } ?: throw AUTHORIZATION_EXCEPTION

            val latestToken: String = conn.stringCommands().get("refresh:$userId".toByteArray())?.let {
                String(it)
            } ?: throw AUTHORIZATION_EXCEPTION

            if (latestToken != refreshToken) {
                throw AUTHORIZATION_EXCEPTION
            }

            userId
        } ?: throw AUTHORIZATION_EXCEPTION

        val user = userService.getUserById(userId.toLong())

        return tokenManager.createToken(user)
    }

    fun generateRefreshToken(loginSecret: String, token: String): String {
        if (loginSecret != this.loginSecret) {
            throw AUTHORIZATION_EXCEPTION
        }
        val userId = tokenManager.getUserId(Token.from(token))

        val user = userService.getUserById(userId)

        return redisTemplate.execute {
            val refreshToken = RefreshToken.from(userId)
            val key = "refresh:${user.id}".toByteArray()

            it.watch(key)

            val oldRefreshToken: ByteArray? = it.stringCommands().get(key)
            it.multi()
            oldRefreshToken?.let { token ->
                it.stringCommands().getDel(token)
            }

            it.stringCommands().set(
                refreshToken.key.toByteArray(),
                refreshToken.userId.toString().toByteArray(),
                Expiration.from(7.days.toJavaDuration()),
                RedisStringCommands.SetOption.UPSERT,
            )
            it.stringCommands().set(
                key,
                refreshToken.key.toByteArray(),
                Expiration.from(7.days.toJavaDuration()),
                RedisStringCommands.SetOption.UPSERT,
            )

            it.exec()
            refreshToken
        }?.key ?: throw IllegalArgumentException("Cannot create refresh token")
    }

    data class RefreshToken(
        val key: String,
        val userId: Long,
    ) {
        companion object {
            private val rng = SecureRandom()
            private val b64 = Base64.getUrlEncoder().withoutPadding()

            fun from(userId: Long): RefreshToken {
                val bytes = ByteArray(32) // 256-bit
                rng.nextBytes(bytes)
                b64.encodeToString(bytes)

                return RefreshToken(
                    key = b64.encodeToString(bytes),
                    userId = userId,
                )
            }
        }
    }
}
