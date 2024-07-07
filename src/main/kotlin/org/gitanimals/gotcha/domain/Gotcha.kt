package org.gitanimals.gotcha.domain

import org.gitanimals.gotcha.core.AggregateRoot
import org.gitanimals.gotcha.domain.response.GotchaResponse

@AggregateRoot
class Gotcha(
    val type: GotchaType,
    val point: Long,
    private val capsules: List<Capsule>,
) {

    fun random(point: Long): GotchaResponse {
        require(point >= this.point) {
            "Not enough point \"$point\" <= \"${this.point}\""
        }
        val gotchaResult = capsules.random()
        return GotchaResponse(
            name = gotchaResult.name,
            ratio = gotchaResult.ratio.toString(),
            point = type.point.toString(),
        )
    }
}
