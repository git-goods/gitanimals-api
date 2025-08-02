package org.gitanimals.identity.app

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import org.gitanimals.core.AUTHORIZATION_EXCEPTION
import org.gitanimals.identity.app.AppleOauth2Api.AppleAuthKeyResponse
import org.gitanimals.identity.domain.EntryPoint
import org.gitanimals.identity.domain.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Component
class AppleLoginFacade(
    private val tokenManager: TokenManager,
    private val userService: UserService,
    private val appleOauth2Api: AppleOauth2Api,
    private val objectMapper: ObjectMapper,
    @Value("\${login.secret}") private val loginSecret: String,
) {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    fun login(loginSecret: String, accessToken: String): String {
        require(this.loginSecret == loginSecret) { throw AUTHORIZATION_EXCEPTION }
        val appleUserInfo = getAppleUserInfo(accessToken)

        val isExistsUser = userService.existsByEntryPointAndAuthenticationId(
            authenticationId = appleUserInfo.sub,
            entryPoint = EntryPoint.APPLE
        )

        val user = when (isExistsUser) {
            true -> userService.getUserByNameAndEntryPoint(appleUserInfo.email, EntryPoint.APPLE)
            false -> {
                userService.newUser(
                    username = appleUserInfo.email,
                    entryPoint = EntryPoint.APPLE,
                    profileImage = defaultProfileImage,
                    contributionPerYears = mapOf(),
                    authenticationId = appleUserInfo.sub,
                )
            }
        }

        return tokenManager.createToken(user).withType()
    }

    private fun getAppleUserInfo(accessToken: String): AppleUserInfo {
        val tokenHeaders = parseHeaders(accessToken)
        val appleAuthKeys = appleOauth2Api.getAuthKeys()
        val publicKey =
            generatePublicKey(tokenHeaders = tokenHeaders, appleAuthKeys = appleAuthKeys)
        val claims = runCatching {
            Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(accessToken)
                .payload
        }.getOrElse {
            logger.error("[AppleLoginFacade] Cannot parse claims from accessToken.")
            throw it
        }

        return AppleUserInfo(
            sub = claims["sub"] as String,
            email = claims["email"] as String,
        )
    }

    private fun parseHeaders(token: String): Map<String, String> = runCatching {
        val encodedHeader: String =
            token
                .split("\\.".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[0]
        val decodedHeader = String(Base64.getUrlDecoder().decode(encodedHeader))
        objectMapper.readValue(
            decodedHeader,
            object : TypeReference<Map<String, String>>() {},
        )
    }.getOrElse {
        logger.error("[AppleLoginFacade] Cannot parse token from header. ${it.message}", it)
        throw it
    }

    private fun generatePublicKey(
        tokenHeaders: Map<String, String>,
        appleAuthKeys: AppleAuthKeyResponse,
    ): PublicKey {
        val publicKeys = appleAuthKeys.keys
        val publicKey = publicKeys.find {
            it.alg == tokenHeaders["alg"] && it.kid == tokenHeaders["kid"]
        } ?: run {
            val message = "Cannot find matched public key."
            logger.error("[AppleLoginFacade] $message alg: \"${tokenHeaders["alg"]}\", kid: \"${tokenHeaders["kid"]}\"")
            error(message)
        }

        val n = Base64.getUrlDecoder().decode(publicKey.n)
        val e = Base64.getUrlDecoder().decode(publicKey.e)

        val publicKeySpec = RSAPublicKeySpec(BigInteger(1, n), BigInteger(1, e))

        val keyFactory = KeyFactory.getInstance(publicKey.kty)

        return keyFactory.generatePublic(publicKeySpec)
    }

    data class AppleUserInfo(
        val sub: String,
        val email: String,
    )

    private companion object {
        private const val defaultProfileImage =
            "https://avatars.githubusercontent.com/u/171903401?s=200&v=4"
    }
}
