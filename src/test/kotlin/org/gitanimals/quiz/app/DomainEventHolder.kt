package org.gitanimals.quiz.app

import io.kotest.matchers.shouldBe
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class DomainEventHolder {

    private val events = mutableMapOf<KClass<*>, Int>()

    @EventListener(Any::class)
    fun listenNewQuizCreated(any: Any) {
        events[any::class] = events.getOrDefault(any::class, 0) + 1
    }

    fun eventsShouldBe(kClass: KClass<*>, count: Int) {
        events[kClass] shouldBe count
    }

    fun deleteAll() = events.clear()
}
