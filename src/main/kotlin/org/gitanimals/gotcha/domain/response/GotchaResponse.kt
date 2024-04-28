package org.gitanimals.gotcha.domain.response

import java.util.*

data class GotchaResponse(
    var id: String? = null,
    val name: String,
    val ratio: String,
    val point: String,
    val idempotency: String = UUID.randomUUID().toString(),
)
