package org.gitanimals.identity.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "user-idempotency")
@Entity(name = "user-idempotency")
class UserIdempotency(
    @Id
    @Column(name = "id")
    val id: String,
)
