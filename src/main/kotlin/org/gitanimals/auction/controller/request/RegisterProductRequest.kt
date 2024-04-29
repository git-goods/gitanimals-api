package org.gitanimals.auction.controller.request

data class RegisterProductRequest(
    val personaId: String,
    val price: String,
)
