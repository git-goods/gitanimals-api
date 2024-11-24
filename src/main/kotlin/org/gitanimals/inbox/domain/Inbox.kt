package org.gitanimals.inbox.domain

import jakarta.persistence.*
import org.gitanimals.inbox.core.AggregateRoot
import org.gitanimals.inbox.core.IdGenerator
import java.time.Instant

@AggregateRoot
@Entity(name = "inbox")
@Table(
    name = "inbox",
    indexes = [Index(name = "inbox_idx_user_id", columnList = "user_id")],
)
class Inbox(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "title")
    val title: String,

    @Column(name = "body")
    val body: String,

    @Embedded
    val publisher: Publisher,

    @Column(name = "read_at")
    private var readAt: Instant?,
) : AbstractTime() {

    fun read() {
        readAt = Instant.now()
    }

    companion object {
        fun of(
            userId: Long,
            title: String,
            body: String,
            publisher: String,
            publishedAt: Instant,
        ): Inbox {
            return Inbox(
                id = IdGenerator.generate(),
                userId = userId,
                publisher = Publisher(
                    publisher = publisher,
                    publishedAt = publishedAt,
                ),
                readAt = null,
                title = title,
                body = body,
            )
        }
    }
}
