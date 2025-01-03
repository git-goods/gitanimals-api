package org.gitanimals.shop.domain

import jakarta.persistence.*
import org.gitanimals.shop.core.AggregateRoot

@AggregateRoot
@Entity(name = "sale")
@Table(name = "sale")
class Sale(
    @Id
    @Column(name = "id")
    val id: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: SaleType,

    @Column(name = "item", nullable = false)
    val item: String,

    @Column(name = "price", nullable = false)
    val price: Long,

    @Column(name = "count", nullable = false)
    private var count: Long,

    @Version
    private var version: Long? = null,
) {

    fun getCount(): Long = this.count

    fun buy() {
        require(this.count > 0) {
            "Cannot buy item : \"$type\" cause its count : \"$count\" == 0"
        }

        this.count -= 1
    }
}
