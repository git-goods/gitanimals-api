package org.gitanimals.inbox.domain

import jakarta.persistence.*
import org.gitanimals.inbox.core.IdGenerator
import org.gitanimals.inbox.core.instant
import java.time.Instant

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

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    val type: InboxType,

    @Column(name = "image")
    val image: String,

    @Column(name = "title")
    val title: String,

    @Column(name = "body")
    val body: String,

    @Column(name = "redirect_to")
    val redirectTo: String,

    @Embedded
    val publisher: Publisher,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private var status: InboxStatus,

    @Column(name = "read_at")
    private var readAt: Instant?,
) : AbstractTime() {

    fun getStatus(): InboxStatus = status

    fun read() {
        readAt = instant()
        status = InboxStatus.READ
    }

    companion object {
        fun of(
            userId: Long,
            title: String,
            body: String,
            publisher: String,
            publishedAt: Instant,
            type: InboxType,
            redirectTo: String,
            image: String,
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
                type = type,
                redirectTo = redirectTo,
                image = image,
                status = InboxStatus.UNREAD,
            )
        }
    }
}
