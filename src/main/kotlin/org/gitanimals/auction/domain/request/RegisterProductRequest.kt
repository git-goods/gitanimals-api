package org.gitanimals.auction.domain.request

data class RegisterProductRequest(
    val sellerId: Long,
    val personaId: Long,
    val personaType: String,
    val personaLevel: Int,
    val price: Long,
)
