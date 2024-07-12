package org.gitanimals.identity.controller.response

data class TotalUserResponse(
    val userCount: String,
) {

    companion object {
        fun from(totalUserCount: Long): TotalUserResponse =
            TotalUserResponse(totalUserCount.toString())
    }
}
