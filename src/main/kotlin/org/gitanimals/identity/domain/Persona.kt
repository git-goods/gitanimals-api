package org.gitanimals.identity.domain

import jakarta.persistence.*

@Table(name = "persona")
@Entity(name = "persona")
class Persona(
    @Id
    @Column(name = "id")
    private val id: Long,

    @Column(name = "type", columnDefinition = "TEXT", nullable = false)
    private val type: String,

    @Column(name = "level", nullable = false)
    private val level: Long,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    val user: User,

    @Version
    private val version: Long,
)
