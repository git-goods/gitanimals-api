package org.gitanimals.identity.infra

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.gitanimals.identity.app.Token
import org.gitanimals.identity.app.TokenManager
import org.gitanimals.identity.domain.User
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class JwtTokenManager : TokenManager {

    private val key = Keys.hmacShaKeyFor(UUID.randomUUID().toString().toByteArray());

    override fun createToken(user: User): Token {
        val value = Jwts.builder()
            .header()
            .add("type", "JWT")
            .and()
            .claims()
            .add(USER_ID, user.id)
            .add(USER_NAME, user.name)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
            .and()
            .signWith(key)
            .compact()

        return Token.bearer(value)
    }

    override fun getUserId(token: Token): Long {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token.value)
            .payload[USER_ID] as Long
    }

    private companion object {
        private const val USER_ID = "userId"
        private const val USER_NAME = "username"
    }
}
