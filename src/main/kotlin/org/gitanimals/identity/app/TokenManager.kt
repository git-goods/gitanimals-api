package org.gitanimals.identity.app

import org.gitanimals.identity.domain.User

fun interface TokenManager {

    fun createToken(user: User): Token
}
