package org.gitanimals.shop.domain

import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SaleService(
    private val saleRepository: SaleRepository,
) {

    fun findAllByType(saleType: SaleType): List<Sale> = saleRepository.findAllByType(saleType)

    @Transactional
    @Retryable(ObjectOptimisticLockingFailureException::class)
    fun buyBySaleTypeAndItem(saleType: SaleType, item: String) {
        val sale = getByTypeAndItem(saleType, item)

        sale.buy()
    }

    fun getByTypeAndItem(saleType: SaleType, item: String): Sale {
        val sale = saleRepository.getByItem(item)

        require(sale.type == saleType) {
            "Cannot find sale by type: \"$saleType\" and item: \"$item\""
        }

        return sale
    }
}
