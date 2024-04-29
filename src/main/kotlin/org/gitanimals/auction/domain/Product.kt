package org.gitanimals.auction.domain

import jakarta.persistence.*
import org.gitanimals.auction.core.IdGenerator
import org.gitanimals.gotcha.core.AggregateRoot

@AggregateRoot
@Table(
    name = "product", indexes = [
        Index(columnList = "seller_id"),
    ]
)
@Entity(name = "product")
class Product(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "seller_id", nullable = false)
    val sellerId: Long,

    @Embedded
    val persona: Persona,

    @Column(name = "price", nullable = false)
    val price: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_state")
    val paymentState: PaymentState,

    @Embedded
    val receipt: Receipt? = null,

    @Version
    private var version: Long? = null,
) : AbstractTime() {

    companion object {

        fun of(
            sellerId: Long,
            personaId: Long,
            personaType: String,
            personaLevel: Int,
            price: Long,
        ): Product = Product(
            id = IdGenerator.generate(),
            sellerId = sellerId,
            persona = Persona(personaId, personaType, personaLevel),
            price = price,
            paymentState = PaymentState.ON_SALE,
        )
    }
}
