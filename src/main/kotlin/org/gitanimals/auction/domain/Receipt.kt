package org.gitanimals.auction.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.Instant

@Embeddable
class Receipt(
    @Column(name = "buyer_id")
    val buyerId: Long,

    @Column(name = "sold_at")
    val soldAt: Instant,
) {
    companion object {
        fun from(buyerId: Long): Receipt =
            Receipt(buyerId, Instant.now())
    }
}
