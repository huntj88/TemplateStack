package me.jameshunt.springtemplate

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.lang.invoke.MethodHandles
import kotlin.coroutines.coroutineContext

inline val logger: ContextLogger
    get() {
        val delegate = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
        return ContextLogger(delegate)
    }

class ContextLogger(private val logger: Logger) {
    suspend fun info(msg: String) {
        MDC.putCloseable(TraceId, coroutineContext.traceId()).use {
            logger.info(msg)
        }
    }

    suspend fun debug(msg: String) {
        MDC.putCloseable(TraceId, coroutineContext.traceId()).use {
            logger.debug(msg)
        }
    }

    suspend fun error(msg: String) {
        MDC.putCloseable(TraceId, coroutineContext.traceId()).use {
            logger.error(msg)
        }
    }

    suspend fun error(msg: String, throwable: Throwable) {
        MDC.putCloseable(TraceId, coroutineContext.traceId()).use {
            logger.error(msg, throwable)
        }
    }
}