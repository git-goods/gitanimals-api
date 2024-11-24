package org.gitanimals.inbox.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.Instant

@Embeddable
class Publisher(
    @Column(name = "publisher")
    val publisher: String,

    @Column(name = "published_at")
    val publishedAt: Instant,
)
