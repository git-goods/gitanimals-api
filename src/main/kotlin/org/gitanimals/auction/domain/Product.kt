package org.gitanimals.auction.domain

import jakarta.persistence.*
import org.gitanimals.gotcha.core.AggregateRoot

@AggregateRoot
@Table(
    name = "product", indexes = [
        Index(columnList = "seller_id"),
        Index(columnList = "persona_id", unique = true),
    ]
)
@Entity(name = "product")
class Product(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "seller_id", nullable = false)
    val sellerId: Long,

    @Column(name = "persona_id", unique = true, nullable = false)
    val personaId: Long,

    @Column(name = "price", nullable = false)
    val price: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_state")
    val paymentState: PaymentState,

    @Embedded
    val receipt: Receipt? = null,

    @Version
    private var version: Long? = null,
) : AbstractTime()
