package org.gitanimals.auction.domain

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("select p from product as p where p.id = :productId")
    fun findByIdWithXForce(@Param("productId") productId: Long): Product?

    @Query(
        """
        select p from product as p where 
          p.id > :lastId 
          and p.state = ProductState.ON_SALE 
          and p.persona.personaType like 
            case 
              when :personaType = 'ALL' then '%' 
              else :personaType 
            end
        """
    )
    fun findAllProducts(
        @Param("lastId") lastId: Long,
        @Param("personaType") personaType: String,
        limit: Pageable,
    ): List<Product>

    @Query(
        """
        select p from product as p where
          p.id > :lastId
          and p.state = ProductState.ON_SALE
          and p.sellerId = :userId
        """
    )
    fun findAllProductsByUserId(
        @Param("userId") userId: Long,
        @Param("lastId") lastId: Long,
        limit: Pageable
    ): List<Product>
}
