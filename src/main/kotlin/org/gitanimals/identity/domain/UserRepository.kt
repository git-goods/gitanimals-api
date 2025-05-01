package org.gitanimals.identity.domain

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByNameAndEntryPoint(name: String, entryPoint: EntryPoint): User?

    fun existsByNameAndEntryPoint(name: String, entryPoint: EntryPoint): Boolean
}
