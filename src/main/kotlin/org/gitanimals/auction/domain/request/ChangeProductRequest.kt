package org.gitanimals.auction.domain.request

data class ChangeProductRequest(
    val id: String,
    val price: String,
)
