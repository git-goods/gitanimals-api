package org.gitanimals.inbox.domain

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@ContextConfiguration(classes = [InboxService::class])
@DisplayName("InboxService 클래스의")
@TestPropertySource("classpath:test.properties")
@EntityScan(basePackages = ["org.gitanimals.inbox.domain"])
@EnableJpaRepositories(basePackages = ["org.gitanimals.inbox.domain"])
internal class InboxServiceTest(
    private val inboxService: InboxService,
    private val inboxRepository: InboxRepository,
) : DescribeSpec({

    describe("findAllUnreadByUserId 메소드는") {
        context("userId를 입력받으면") {
            val userId = 1L
            inboxRepository.saveAndFlush(inbox(userId = userId))
            inboxRepository.saveAndFlush(inbox(userId = userId))

            it("읽지 않은 모든 Inbox를 조회한다.") {
                val result = inboxService.findAllUnreadByUserId(userId)

                result.inboxes.size shouldBe 2
            }
        }
    }

    describe("readById 메소드는") {
        context("userId와 inbox id를 입력받으면") {
            val userId = 2L
            val inbox1 = inboxRepository.saveAndFlush(inbox(userId = userId))
            val inbox2 = inboxRepository.saveAndFlush(inbox(userId = userId))

            it("userId와 id에 해당하는 inbox를 읽음 처리한다") {
                inboxService.readById(userId, inbox1.id)
                val result = inboxService.findAllUnreadByUserId(userId)

                result.inboxes.size shouldBe 1
            }
        }
    }
})
