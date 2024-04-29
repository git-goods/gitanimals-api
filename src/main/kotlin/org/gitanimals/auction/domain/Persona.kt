package org.gitanimals.auction.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Persona(
    @Column(name = "persona_id", unique = true, nullable = false)
    val personaId: Long,

    @Column(name = "persona_type", nullable = false)
    val personaType: String,

    @Column(name = "persona_level", nullable = false)
    val personaLevel: Int,
)
