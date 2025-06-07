package org.gitanimals.identity.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long> {

    @Query(
        """
            select u from users as u 
            where u.name = :name
            and u.authInfo.entryPoint = :entryPoint
        """
    )
    fun findByNameAndEntryPoint(
        @Param("name") name: String,
        @Param("entryPoint") entryPoint: EntryPoint,
    ): User?

    @Query(
        """
            select u from users as u
            where u.authInfo.entryPoint = :entryPoint
            and u.authInfo.authenticationId = :authenticationId
        """
    )
    fun findByEntryPointAndAuthenticationId(
        @Param("entryPoint") entryPoint: EntryPoint,
        @Param("authenticationId") authenticationId: String,
    ): User?

    fun existsByNameAndAuthInfoEntryPoint(name: String, entryPoint: EntryPoint): Boolean

}
