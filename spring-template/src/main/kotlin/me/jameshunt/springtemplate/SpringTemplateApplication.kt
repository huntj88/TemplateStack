package me.jameshunt.springtemplate

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.sql.DataSource

@SpringBootApplication
class SpringTemplateApplication

typealias UserId = UUID

fun main(args: Array<String>) {
    runApplication<SpringTemplateApplication>(*args)
}

@Configuration
class DataSourceConfig {
    @Bean
    fun getDataSource(): DataSource {
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

@Throws(IllegalArgumentException::class)
fun String.toUUID(): UUID = UUID.fromString(this).also {
    if (it.toString() != this) throw IllegalArgumentException("Invalid UUID")
}