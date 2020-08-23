package me.jameshunt.app

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import java.util.*

fun Application.installErrorHandling() {
    install(StatusPages) {
        exception<Throwable> { cause ->
            when(cause) {
                is ApiException -> {
                    val response = ErrorResponse(
                        statusCode = cause.statusCode.value,
                        traceId = call.callId?.let { UUID.fromString(it) },
                        message = cause.userMessage
                    )
                    call.respond(cause.statusCode, response)
                }
                else -> {
                    val response = ErrorResponse(
                        statusCode = HttpStatusCode.InternalServerError.value,
                        traceId = call.callId?.let { UUID.fromString(it) },
                        message = HttpStatusCode.InternalServerError.description
                    )
                    call.respond(HttpStatusCode.InternalServerError, response)
                }
            }
            cause.printStackTrace() // TODO: only printStackTrace locally
            throw cause
        }
    }
}

data class ErrorResponse(
    val statusCode: Int,
    val traceId: UUID?,
    val message: String
)

class ApiException(
    val statusCode: HttpStatusCode,
    val userMessage: String = statusCode.description
): RuntimeException()