package org.gitanimals.auction.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.gitanimals.auction.core.IdGenerator
import org.gitanimals.gotcha.core.AggregateRoot
import java.time.Instant

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
    private var paymentState: PaymentState,

    @Embedded
    private var receipt: Receipt? = null,

    @Version
    private var version: Long? = null,
) : AbstractTime() {

    fun getPaymentState(): PaymentState = this.paymentState

    @JsonIgnore
    fun getBuyerId(): Long? = receipt?.buyerId

    @JsonIgnore
    fun getSoldAt(): Instant? = receipt?.soldAt

    fun buy(buyerId: Long) {
        require(paymentState == PaymentState.ON_SALE) {
            "Cannot buy product cause it's already \"$paymentState\""
        }
        this.paymentState = PaymentState.SOLD_OUT
        this.receipt = Receipt.from(buyerId)
    }

    fun onSales() {
        this.paymentState = PaymentState.ON_SALE
        this.receipt = null
    }

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
