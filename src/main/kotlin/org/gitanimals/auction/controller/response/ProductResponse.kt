package org.gitanimals.auction.controller.response

import com.fasterxml.jackson.annotation.JsonFormat
import org.gitanimals.auction.domain.Product
import org.gitanimals.auction.domain.ProductState
import java.time.LocalDateTime
import java.time.ZoneOffset

data class ProductResponse(
    val id: String,
    val sellerId: String,
    val persona: Persona,
    val price: String,
    val paymentState: ProductState,
    val receipt: Receipt? = null,
) {

    data class Persona(
        val personaId: String,
        val personaType: String,
        val personaLevel: Int,
    )

    data class Receipt(
        private var buyerId: String,
        @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "UTC"
        )
        private var soldAt: LocalDateTime,
    )

    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                id = product.id.toString(),
                sellerId = product.sellerId.toString(),
                persona = Persona(
                    personaId = product.persona.personaId.toString(),
                    personaType = product.persona.personaType,
                    personaLevel = product.persona.personaLevel,
                ),
                price = product.getPrice().toString(),
                paymentState = product.getState(),
                receipt = when (product.getState() == ProductState.SOLD_OUT) {
                    true -> Receipt(
                        product.getBuyerId()!!.toString(),
                        LocalDateTime.ofInstant(product.getSoldAt()!!, ZoneOffset.UTC)
                    )

                    else -> null
                }
            )
        }
    }
}
