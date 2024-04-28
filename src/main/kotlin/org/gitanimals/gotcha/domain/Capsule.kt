package org.gitanimals.gotcha.domain

import kotlin.math.max

class Capsule(
    val name: String,

    val ratio: Long,
) {

    companion object {
        fun of(name: String, ratio: Double): Capsule {
            val count = max(1.0, ratio * 1000)

            return Capsule(name, count.toLong())
        }
    }
}
