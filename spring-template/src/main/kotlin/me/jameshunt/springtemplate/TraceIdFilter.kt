package me.jameshunt.springtemplate

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.ReactorContext
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*
import kotlin.coroutines.CoroutineContext

const val TraceId = "TraceId"

@Component
class TraceIdFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val headerTraceId = exchange.request.headers[TraceId]?.firstOrNull()
        val traceId: String = headerTraceId ?: UUID.randomUUID().toString()
        return chain
            .filter(exchange)
            .subscriberContext { it.put(TraceId, traceId) }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineContext.traceId(): String {
    return this[ReactorContext]?.context?.get<String>(TraceId) ?: throw IllegalStateException("TraceId Missing")
}