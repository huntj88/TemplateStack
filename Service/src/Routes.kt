package me.jameshunt

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

class UserRoutes @Inject constructor() {
    fun Routing.setup() {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
    }
}