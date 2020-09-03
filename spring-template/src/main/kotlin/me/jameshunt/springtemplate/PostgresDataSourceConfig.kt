package me.jameshunt.springtemplate

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.jameshunt.jdbc.extension.executeParameterizedQuery
import me.jameshunt.jdbc.extension.postgresDialect
import org.springframework.boot.actuate.health.AbstractHealthIndicator
import org.springframework.boot.actuate.health.Health
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class PostgresDataSourceConfig {
    @Bean
    fun postgresDataSource(): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/template"
            username = "ryanIsFunny"
            password = "ryanIsFunnyLooking"
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        }

        return HikariDataSource(config)
    }

    @Bean
    fun postgresHealth(dataSource: DataSource): AbstractHealthIndicator {
        return object : AbstractHealthIndicator() {
            override fun doHealthCheck(builder: Health.Builder) {
                dataSource.connection.postgresDialect().executeParameterizedQuery(
                    query = "SELECT NOW() as now",
                    setArgs = {},
                    handleResults = { results ->
                        results.next()
                        results.getTimestamp("now")?.toInstant()
                    }
                )
                builder.up().build()
            }
        }
    }
}