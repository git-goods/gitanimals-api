package org.gitanimals.auction.domain

enum class ProductOrderType(
    val fieldName: String
) {

    CREATED_AT("id"),
    PRICE("price"),
    SOLD_AT("receipt.soldAt"),
    LEVEL("persona.personaLevel"),
    ;

    companion object {
        fun fromString(fieldName: String) =
            ProductOrderType.valueOf(fieldName.uppercase()).fieldName
    }

}
