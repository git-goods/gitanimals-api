package org.gitanimals.identity.domain

import org.gitanimals.identity.core.IdGenerator

fun user(
    id: Long = IdGenerator.generate(),
    name: String = "devxb",
    points: Long = 0L,
): User {
    return User(
        id = id,
        name = name,
        points = points,
    )
}
