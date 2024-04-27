package org.gitanimals.gotcha.domain

import org.gitanimals.gotcha.core.AggregateRoot
import org.gitanimals.gotcha.domain.response.GotchaResponse

@AggregateRoot
class Gotcha(
    val type: GotchaType,
    val capsules: List<Capsule> = type.getCapsules(),
) {

    fun random(): GotchaResponse {
        val gotchaResult = capsules.random()
        return GotchaResponse(gotchaResult.name, gotchaResult.ratio.toString())
    }

    companion object {
        fun createDefault(): Gotcha = Gotcha(GotchaType.DEFAULT, GotchaType.DEFAULT.getCapsules())
    }
}
