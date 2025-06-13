package org.gitanimals.identity.domain

fun user(
    name: String = "devxb",
    points: Long = 0L,
    profileImage: String = "some image",
    entryPoint: EntryPoint = EntryPoint.GITHUB,
    authenticationId: String = name,
): User = User.newUser(
    name = name,
    points = points,
    profileImage = profileImage,
    entryPoint = entryPoint,
    authenticationId = authenticationId,
)
