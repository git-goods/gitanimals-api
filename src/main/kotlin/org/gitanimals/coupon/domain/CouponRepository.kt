package org.gitanimals.coupon.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CouponRepository : JpaRepository<Coupon, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from coupon as c where code = :code and c.userId = :userId")
    fun deleteByUserIdAndCode(@Param("userId") userId: Long, @Param("code") code: String)

    fun existsByUserIdAndCode(userId: Long, code: String): Boolean

    fun findAllByUserId(userId: Long): List<Coupon>
}
