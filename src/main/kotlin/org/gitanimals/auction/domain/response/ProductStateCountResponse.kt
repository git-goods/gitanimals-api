package org.gitanimals.auction.domain.response

import org.gitanimals.auction.domain.ProductState

data class ProductStateCountResponse(
    val state: ProductState,
    val count: Long,
)
