package org.gitanimals.identity.domain

import org.gitanimals.identity.core.IdGenerator
import org.gitanimals.identity.domain.value.Contribution

fun user(
    id: Long = IdGenerator.generate(),
    name: String = "devxb",
    points: Long = 0L,
    contributions: MutableList<Contribution> = mutableListOf(),
    personas: MutableList<Persona> = mutableListOf(),
): User {
    return User(
        id = id,
        name = name,
        points = points,
        contributions = contributions,
        personas = personas,
    )
}
