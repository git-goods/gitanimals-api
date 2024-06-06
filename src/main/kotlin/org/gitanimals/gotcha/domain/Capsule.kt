package org.gitanimals.gotcha.domain

class Capsule(
    val name: String,
    val ratio: Double,
) {

    companion object {
        fun of(name: String, ratio: Double): Capsule {
            return Capsule(name, ratio)
        }
    }
}
