package org.gitanimals.core

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.slf4j.MDCContext
import org.gitanimals.core.GracefulShutdownDispatcher.graceFulShutdownExecutorServices
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object GracefulShutdownDispatcher {

    val graceFulShutdownExecutorServices: MutableList<ExecutorService> = mutableListOf()

    private val executorService = Executors.newFixedThreadPool(10) { runnable ->
        Thread(runnable, "gitanimals-gracefulshutdown").apply { isDaemon = false }
    }.withGracefulShutdown()

    private val defaultDispatcher: CoroutineDispatcher = executorService.asCoroutineDispatcher()

    fun ExecutorService.withGracefulShutdown(): ExecutorService {
        graceFulShutdownExecutorServices.add(this)
        return this
    }

    fun gracefulLaunch(
        dispatcher: CoroutineDispatcher = defaultDispatcher,
        block: suspend CoroutineScope.() -> Unit
    ) {
        CoroutineScope(dispatcher + MDCContext()).launch(block = block)
    }
}

@Component
class GracefulShutdownHook {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    @PreDestroy
    fun tryGracefulShutdown() {
        logger.info("Shutting down dispatcher...")
        graceFulShutdownExecutorServices.forEach {
            it.shutdown()
        }
        runCatching {
            if (
                graceFulShutdownExecutorServices.any {
                    it.awaitTermination(60, TimeUnit.SECONDS).not()
                }
            ) {
                logger.warn("Forcing shutdown...")
                graceFulShutdownExecutorServices.forEach {
                    it.shutdown()
                }
            } else {
                logger.info("Shutdown completed gracefully.")
            }
        }.onFailure {
            if (it is InterruptedException) {
                logger.warn("Shutdown interrupted. Forcing shutdown...")
                graceFulShutdownExecutorServices.forEach {
                    it.shutdownNow()
                }
                Thread.currentThread().interrupt()
            }
        }
    }
}
