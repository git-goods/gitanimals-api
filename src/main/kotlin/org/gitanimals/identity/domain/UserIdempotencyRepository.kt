package org.gitanimals.identity.domain

import org.springframework.data.jpa.repository.JpaRepository

interface UserIdempotencyRepository : JpaRepository<UserIdempotency, String>
