package me.jameshunt.app

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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
import java.util.*
import javax.inject.Singleton
import javax.sql.DataSource

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dependencyGraph = DaggerApplicationGraph.create()

    installErrorHandling()

    install(CallId) {
        generate { UUID.randomUUID().toString() }
    }

    install(CallLogging) {
        callIdMdc("traceId")
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(Authentication) {
        // todo
    }

    install(ContentNegotiation) {
        val converter = JacksonConverter(dependencyGraph.objectMapper())
        register(ContentType.Application.Json, converter)
    }

    dependencyGraph.routes().apply { setupRoutes() }
}

@Singleton
@Component(modules = [ConfigurableSingletons::class])
interface ApplicationGraph {
    fun objectMapper(): ObjectMapper
    fun routes(): Routes
}

@Module
class ConfigurableSingletons {
    @Singleton
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

    @Singleton
    @Provides
    fun providePostgresDataSource(): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:postgresql://localhost:5432/template"
        config.username = "ryanIsFunny"
        config.password = "ryanIsFunnyLooking"
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        return HikariDataSource(config)
    }
}