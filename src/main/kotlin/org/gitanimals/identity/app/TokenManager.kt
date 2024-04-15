package org.gitanimals.identity.app

import org.gitanimals.identity.domain.User

interface TokenManager {

    fun createToken(user: User): Token

    fun getUserId(token: Token): Long
}
