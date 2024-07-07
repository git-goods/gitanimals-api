package org.gitanimals.gotcha.domain.response

import java.util.*

data class GotchaResponse(
    var id: String? = null,
    val name: String,
    val ratio: String,
    val point: Long,
    val idempotency: String = UUID.randomUUID().toString(),
) {
    constructor(
        name: String,
        ratio: String,
        point: String,
    ) : this(null, name = name, ratio = ratio, point = point.toLong())
}
