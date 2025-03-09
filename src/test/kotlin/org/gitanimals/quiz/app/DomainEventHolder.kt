package org.gitanimals.quiz.app

import io.kotest.matchers.shouldBe
import org.gitanimals.quiz.infra.event.NewQuizCreated
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class DomainEventHolder {

    private val events = mutableMapOf<KClass<*>, Int>()

    @EventListener(NewQuizCreated::class)
    fun listenNewQuizCreated(newQuizCreated: NewQuizCreated) {
        events[newQuizCreated::class] = events.getOrDefault(newQuizCreated::class, 0) + 1
    }

    fun eventsShouldBe(kClass: KClass<*>, count: Int) {
        events[kClass] shouldBe count
    }

    fun deleteAll() = events.clear()
}
