package org.gitanimals.auction.controller.response

import org.gitanimals.auction.domain.PaymentState
import org.gitanimals.auction.domain.Product
import java.time.Instant

data class ProductResponse(
    val id: String,
    val sellerId: String,
    val persona: Persona,
    val price: String,
    val paymentState: PaymentState,
    val receipt: Receipt? = null,
) {

    data class Persona(
        val personaId: String,
        val personaType: String,
        val personaLevel: Int,
    )

    data class Receipt(
        private var buyerId: String,
        private var soldAt: Instant,
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
                price = product.price.toString(),
                paymentState = product.getPaymentState(),
                receipt = when (product.getPaymentState() == PaymentState.SOLD_OUT) {
                    true -> Receipt(product.getBuyerId()!!.toString(), product.getSoldAt()!!)
                    else -> null
                }
            )
        }
    }
}
