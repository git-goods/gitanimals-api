package org.gitanimals.auction.app.event

import org.gitanimals.auction.domain.Product
import java.time.Instant

data class InboxInputEvent(
    val inboxData: InboxData,
    val publisher: Publisher,
) {

    data class Publisher(
        val publisher: String,
        val publishedAt: Instant,
    )

    data class InboxData(
        val userId: Long,
        val type: String = "INBOX",
        val title: String,
        val body: String,
        val image: String,
        val redirectTo: String,
    )

    companion object {
        fun createSoldOutInbox(sellerName: String, product: Product): InboxInputEvent {
            return InboxInputEvent(
                inboxData = InboxData(
                    userId = product.sellerId,
                    title = "펫이 판매되었어요",
                    body = "$sellerName 님의 펫이 ${product.getPrice()}원에 판매되었습니다.",
                    image = "https://avatars.githubusercontent.com/u/171903401?s=200&v=4",
                    redirectTo = "NO_REDIRECT",
                ),
                publisher = Publisher(
                    publisher = "AUCTION",
                    publishedAt = Instant.now(),
                )
            )
        }
    }
}
