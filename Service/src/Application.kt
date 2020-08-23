package me.jameshunt

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Component
import dagger.Module
import dagger.Provides
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.jackson.*
import io.ktor.response.*
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dependencyGraph = DaggerApplicationGraph.create()

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
            cause.printStackTrace() // TODO: only printStackTrace locally
            throw cause
        }
    }

    install(CallLogging) {
        mdc("traceId") { UUID.randomUUID().toString() }
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(Authentication) {
    }

    install(ContentNegotiation) {
        val converter = JacksonConverter(dependencyGraph.objectMapper())
        register(ContentType.Application.Json, converter)
    }

    dependencyGraph.routes().apply { setupRoutes() }
}

@Component(modules = [ConfigurableSingletons::class])
interface ApplicationGraph {
    fun objectMapper(): ObjectMapper
    fun routes(): Routes
}

@Module
class ConfigurableSingletons {
    @Provides
    fun provideObjectMapper(): ObjectMapper {
        val mapper = jacksonObjectMapper()
        return mapper.apply {
            setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            })
            enable(SerializationFeature.INDENT_OUTPUT)
            registerModule(JavaTimeModule())
        }
    }
}