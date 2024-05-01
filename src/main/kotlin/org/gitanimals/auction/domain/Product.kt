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
    private var price: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state")
    private var state: ProductState,

    @Embedded
    private var receipt: Receipt? = null,

    @Version
    private var version: Long? = null,
) : AbstractTime() {

    fun getProductState(): ProductState = this.state

    fun getPrice(): Long = this.price

    @JsonIgnore
    fun getBuyerId(): Long? = receipt?.buyerId

    @JsonIgnore
    fun getSoldAt(): Instant? = receipt?.soldAt

    fun buy(buyerId: Long) {
        require(state == ProductState.ON_SALE) {
            "Cannot buy product cause it's already \"$state\""
        }
        this.state = ProductState.SOLD_OUT
        this.receipt = Receipt.from(buyerId)
    }

    fun waitDelete() {
        require(state != ProductState.SOLD_OUT) {
            "Cannot delete product cause it's already \"$state\""
        }
        this.state = ProductState.WAIT_DELETE
    }

    fun onSales() {
        this.state = ProductState.ON_SALE
        this.receipt = null
    }

    fun changePrice(price: Long) {
        this.price = price
        require(this.price >= 1) { "Price must be higher than 1" }
    }

    init {
        require(this.price >= 1) { "Price must be higher than 1" }
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
            state = ProductState.ON_SALE,
        )
    }
}
