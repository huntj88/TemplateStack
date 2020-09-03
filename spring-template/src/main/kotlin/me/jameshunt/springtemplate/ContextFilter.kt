package me.jameshunt.springtemplate

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * puts context in exchange attributes for error handling
 */

@Component
class ContextFilter: WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return Mono.subscriberContext().flatMap {
            exchange.attributes["context"] = it
            chain.filter(exchange)
        }
    }
}