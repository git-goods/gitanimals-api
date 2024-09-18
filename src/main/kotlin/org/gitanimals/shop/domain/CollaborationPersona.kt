package org.gitanimals.shop.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.gitanimals.shop.core.AggregateRoot

@Entity
@AggregateRoot
@Table(name = "collaboration_persona")
class CollaborationPersona(
    @Id
    @Column(name = "name", columnDefinition = "TEXT", nullable = false)
    val name: String,

    @Column(name = "price", nullable = false)
    val price: Long,

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    val description: String,
)
