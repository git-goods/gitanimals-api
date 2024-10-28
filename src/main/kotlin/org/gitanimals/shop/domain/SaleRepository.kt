package org.gitanimals.shop.domain

import org.springframework.data.jpa.repository.JpaRepository

interface SaleRepository : JpaRepository<Sale, Long> {
    fun getByItem(item: String): Sale

    fun findAllByType(saleType: SaleType): List<Sale>
}
