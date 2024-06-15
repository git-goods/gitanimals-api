package org.gitanimals.auction.domain

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {

    @Query("select p from product as p where p.persona.personaId = :personaId and p.state != ProductState.SOLD_OUT")
    fun findByPersonaId(@Param("personaId") personaId: Long): Product?

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("select p from product as p where p.id = :productId")
    fun findByIdWithXForce(@Param("productId") productId: Long): Product?

    @Query(
        """
        select p from product as p where
          p.state = ProductState.ON_SALE 
          and p.persona.personaType like 
            case 
              when :personaType = 'ALL' then '%' 
              else :personaType 
            end
        """
    )
    fun findAllProducts(
        @Param("personaType") personaType: String,
        page: Pageable,
    ): Page<Product>

    @Query(
        """
        select p from product as p where
          p.state = ProductState.ON_SALE
          and p.sellerId = :userId
        """
    )
    fun findAllProductsByUserId(
        @Param("userId") userId: Long,
        limit: Pageable
    ): Page<Product>

    @Query(
        """
        select p from product as p where 
          p.state = ProductState.SOLD_OUT 
          and p.persona.personaType like 
            case 
              when :personaType = 'ALL' then '%' 
              else :personaType 
            end
        """
    )
    fun findAllProductHistories(
        @Param("personaType") personaType: String,
        limit: Pageable,
    ): Page<Product>
}
