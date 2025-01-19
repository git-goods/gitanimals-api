package org.gitanimals.core

import org.gitanimals.core.filter.MDCFilter.Companion.TRACE_ID
import org.rooftop.netx.api.Context
import org.rooftop.netx.api.ContextOrchestrate
import org.rooftop.netx.api.ContextRollback
import org.slf4j.MDC

open class TraceIdContextOrchestrator<T : Any, V : Any>(
    private val orchestrate: ContextOrchestrate<T, V>,
) : ContextOrchestrate<T, V> {

    override fun orchestrate(context: Context, request: T): V {
        MDC.put(TRACE_ID, context.decodeContext(TRACE_ID, String::class))
        return orchestrate.orchestrate(context, request).also {
            MDC.remove(TRACE_ID)
        }
    }
}

open class TraceIdContextRollback<T : Any, V : Any?>(
    private val rollback: ContextRollback<T, V>,
) : ContextRollback<T, V> {

    override fun rollback(context: Context, request: T): V {
        MDC.put(TRACE_ID, context.decodeContext(TRACE_ID, String::class))
        return rollback.rollback(context, request).also {
            MDC.remove(TRACE_ID)
        }
    }
}
