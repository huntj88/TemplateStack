package me.jameshunt.app

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import javax.inject.Inject

class Routes @Inject constructor(private val userRoutes: UserRoutes) {

    fun Application.setupRoutes() {
        routing {
            userRoutes.apply { setup() }
        }
    }
}

class UserRoutes @Inject constructor(private val userRepository: UserRepository) {
    fun Routing.setup() {
        get("/") {
            throw ApiException(HttpStatusCode.Unauthorized, "haha")
            call.respond("HELLO WORLD!")
        }

        get("/users") {
            val users = userRepository.getUsers()
            call.respond(users)
        }
    }
}
