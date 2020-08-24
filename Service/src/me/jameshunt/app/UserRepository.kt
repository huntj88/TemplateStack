package me.jameshunt.app

import com.github.michaelbull.jdbc.context.connection
import com.github.michaelbull.jdbc.executeParameterizedQuery
import com.github.michaelbull.jdbc.map
import com.github.michaelbull.jdbc.postgresDialect
import com.github.michaelbull.jdbc.withRepositoryContext
import java.util.*
import javax.inject.Inject
import javax.sql.DataSource

class UserRepository @Inject constructor(private val dataSource: DataSource) {

    suspend fun getUsers(): List<User> = withRepositoryContext(dataSource) {
        coroutineContext.connection.postgresDialect().executeParameterizedQuery(
            query = "SELECT * FROM users",
            setArgs = {},
            handleResults = { results ->
                results.map {
                    User(
                        userId = it.getString("user_id").toUUID(),
                        firstName = it.getString("first_name"),
                        lastName = it.getString("last_name")
                    )
                }
            }
        )
    }
}

typealias UserId = UUID
data class User(
    val userId: UserId,
    val firstName: String,
    val lastName: String,
)