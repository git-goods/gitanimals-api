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
    private var productState: ProductState,

    @Embedded
    private var receipt: Receipt? = null,

    @Version
    private var version: Long? = null,
) : AbstractTime() {

    fun getProductState(): ProductState = this.productState

    fun getPrice(): Long = this.price

    @JsonIgnore
    fun getBuyerId(): Long? = receipt?.buyerId

    @JsonIgnore
    fun getSoldAt(): Instant? = receipt?.soldAt

    fun buy(buyerId: Long) {
        require(productState == ProductState.ON_SALE) {
            "Cannot buy product cause it's already \"$productState\""
        }
        this.productState = ProductState.SOLD_OUT
        this.receipt = Receipt.from(buyerId)
    }

    fun waitDelete() {
        require(productState != ProductState.SOLD_OUT) {
            "Cannot delete product cause it's already \"$productState\""
        }
        this.productState = ProductState.WAIT_DELETE
    }

    fun onSales() {
        this.productState = ProductState.ON_SALE
        this.receipt = null
    }

    fun changePrice(price: Long) {
        this.price = price
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
            productState = ProductState.ON_SALE,
        )
    }
}
