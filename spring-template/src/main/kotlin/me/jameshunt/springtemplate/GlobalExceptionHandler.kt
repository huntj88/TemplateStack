package me.jameshunt.springtemplate

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.util.context.Context

@Component
@Order(-1)
class GlobalExceptionHandler(private val appErrorWriter: AppErrorWriter) : ErrorWebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        if (exchange.response.isCommitted) {
            return Mono.error(ex)
        }

        val traceId: String = exchange.attributes["context"]?.let { it as Context }?.get(TraceId) ?: "Missing TraceId"
        val appError = when(ex) {
            is ResponseStatusException -> AppError(
                status = ex.status,
                exception = ex,
                message = ex.reason ?: ex.status.reasonPhrase,
                traceId = traceId
            )
            else -> AppError(
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                exception = ex,
                message = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                traceId = traceId
            )
        }
        return appErrorWriter.write(exchange, appError)
    }
}

@Component
class AppErrorWriter(private val objectMapper: ObjectMapper) {
    fun write(exchange: ServerWebExchange, error: AppError): Mono<Void> {
        return exchange.response.writeWith(Mono.fromSupplier {
            val bufferFactory = exchange.response.bufferFactory()
            try {
                exchange.response.statusCode = error.status
                MDC.putCloseable(TraceId, error.traceId).use {
                    MDC.put("path", exchange.request.path.value())
                    MDC.put("method", exchange.request.method!!.name)
                    LoggerFactory.getLogger(this::class.java).error(error.message, error.exception)
                }
                exchange.response.headers.contentType = MediaType.APPLICATION_JSON
                return@fromSupplier bufferFactory.wrap(objectMapper.writeValueAsBytes(error))
            } catch (ex: Exception) {
                MDC.putCloseable(TraceId, error.traceId).use {
                    LoggerFactory.getLogger(this::class.java).error("Error writing error response", ex)
                }
                return@fromSupplier bufferFactory.wrap(ByteArray(0))
            }
        })
    }
}

data class AppError(
    @JsonIgnore
    val status: HttpStatus,
    @JsonIgnore
    val exception: Throwable,
    val message: String,
    val traceId: String
) {
    val code: Int = status.value()
}