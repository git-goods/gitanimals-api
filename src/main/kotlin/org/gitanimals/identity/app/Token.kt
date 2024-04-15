package org.gitanimals.identity.app

class Token private constructor(
    val type: Type,
    val value: String
) {

    fun withType(): String = "${type.value} $value"

    companion object {

        fun from(token: String): Token {
            return runCatching {
                val tokensParts = token.split(" ")
                Token(Type.valueOf(tokensParts[0].uppercase()), tokensParts[1])
            }.getOrElse {
                throw IllegalArgumentException("Cannot create token from \"$token\"", it)
            }
        }

        fun bearer(value: String): Token = Token(Type.BEARER, value)
    }

    enum class Type(val value: String) {
        BEARER("bearer")
    }
}
