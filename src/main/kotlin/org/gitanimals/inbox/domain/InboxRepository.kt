package org.gitanimals.inbox.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface InboxRepository : JpaRepository<Inbox, Long> {

    @Query(
        """
            select i from inbox as i
            where i.userId = :userId
        """
    )
    fun findByUserId(@Param("userId") userId: Long): List<Inbox>

    @Query(
        """
            select i from inbox as i
            where i.id = :id
            and i.userId = :userId
        """
    )
    fun findByIdAndUserId(@Param("id") id: Long, @Param("userId") userId: Long): Inbox?

    @Query(
        """
            delete from inbox as i where i.createdAt <= :expirationDate
        """
    )
    @Modifying(
        clearAutomatically = true,
        flushAutomatically = true,
    )
    fun deleteExpiredInboxes(@Param("expirationDate") expirationDate: Instant)
}
