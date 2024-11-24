package org.gitanimals.inbox.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InboxRepository: JpaRepository<Inbox, Long> {

    @Query(
        """
            select InboxApplcation(i.userId, i) from inbox as i
            where i.userId = :userId 
            and i.readAt is null
        """
    )
    fun findAllUnReadByUserId(@Param("userId") userId: Long): InboxApplication
}
