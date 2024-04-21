package org.gitanimals.coupon.domain

import org.springframework.data.jpa.repository.JpaRepository

interface IdempotencyRepository : JpaRepository<Idempotency, String>
