package org.gitanimals.identity.app

class Token private constructor(
    val type: Type,
    val value: String
) {

    fun withType(): String = "${type.value} $value"

    companion object {
        fun bearer(value: String): Token = Token(Type.BEARER, value)
    }

    enum class Type(val value: String) {
        BEARER("bearer")
    }
}
