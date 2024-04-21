package org.gitanimals.identity.controller.response

import org.gitanimals.identity.domain.User

data class UserResponse(
    val id: String,
    val username: String,
    val points: String,
    val profileImage: String,
) {

    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                user.id.toString(),
                user.name,
                user.getPoints().toString(),
                user.profileImage,
            )
        }
    }
}
