package org.gitanimals.identity.controller.response

import org.gitanimals.identity.domain.EntryPoint
import org.gitanimals.identity.domain.User

data class UserResponse(
    val id: String,
    val username: String,
    val points: String,
    val profileImage: String,
    val entryPoint: EntryPoint,
) {

    companion object {

        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id.toString(),
                username = user.name,
                points = user.getPoints().toString(),
                profileImage = user.profileImage,
                entryPoint = user.entryPoint,
            )
        }
    }
}
