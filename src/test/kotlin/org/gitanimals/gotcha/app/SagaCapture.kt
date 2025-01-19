package org.gitanimals.gotcha.app

import io.kotest.matchers.equals.shouldBeEqual
import org.rooftop.netx.api.SagaRollbackEvent
import org.rooftop.netx.api.SagaRollbackListener
import org.rooftop.netx.meta.SagaHandler

@SagaHandler
class SagaCapture {

    val storage = mutableMapOf<String, Int>()

    fun rollbackCountShouldBe(count: Int) {
        storage["rollback"]!! shouldBeEqual count
    }

    @SagaRollbackListener
    fun captureRollback(rollbackEvent: SagaRollbackEvent) {
        storage["rollback"] = (storage["rollback"] ?: 0) + 1
    }

    fun clear() {
        storage.clear()
    }
}
