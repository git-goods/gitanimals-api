package org.gitanimals.identity.controller.response

import org.gitanimals.identity.domain.User

data class UserResponse(
    val id: String,
    val username: String,
    val points: String,
) {

    companion object {
        fun of(user: User): UserResponse {
            return UserResponse(
                user.id.toString(),
                user.name,
                user.getPoints().toString()
            )
        }
    }
}
