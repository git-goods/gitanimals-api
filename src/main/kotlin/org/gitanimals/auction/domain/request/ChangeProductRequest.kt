package org.gitanimals.auction.domain.request

data class ChangeProductRequest(
    val id: Long,
    val price: Long,
)
