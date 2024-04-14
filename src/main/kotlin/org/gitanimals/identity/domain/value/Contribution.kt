package org.gitanimals.identity.domain.value

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Contribution(
    @Column(name = "years", nullable = false)
    val year: Int,
    @Column(name = "contributions", nullable = false)
    var contribution: Int,
)
