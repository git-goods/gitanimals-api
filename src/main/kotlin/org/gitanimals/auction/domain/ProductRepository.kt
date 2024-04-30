package org.gitanimals.auction.domain

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("select p from product as p where p.id = :productId")
    fun findByIdWithXForce(@Param("productId") productId: Long): Product?
}
