package org.gitanimals.auction.domain

import org.gitanimals.auction.domain.response.ProductStateCountResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface ProductStatisticRepository : JpaRepository<Product, Long> {

    @Query(
        """
            select new org.gitanimals.auction.domain.response.ProductStateCountResponse(p.state, count(p.id)) from product p 
            where p.modifiedAt between :startDay and :endDay 
            group by p.state
        """
    )
    fun getDailyCountPerState(
        @Param("startDay") startDay: Instant,
        @Param("endDay") endDay: Instant,
    ): List<ProductStateCountResponse>

    @Query("select new org.gitanimals.auction.domain.response.ProductStateCountResponse(p.state, count(p.id)) from product p group by p.state")
    fun getTotalCountPerState(): List<ProductStateCountResponse>
}
