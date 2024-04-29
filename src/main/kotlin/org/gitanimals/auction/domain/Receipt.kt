package org.gitanimals.auction.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.Instant

@Embeddable
class Receipt(
    @Column(name = "buyer_id")
    private var buyerId: Long,

    @Column(name = "sold_at")
    private var soldAt: Instant,
)
