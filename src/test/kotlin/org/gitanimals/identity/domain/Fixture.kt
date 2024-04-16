package org.gitanimals.identity.domain

fun user(
    name: String = "devxb",
    points: Long = 0L,
    profileImage: String = "some image",
): User = User.newUser(
    name = name,
    points = points,
    profileImage = profileImage,
)
