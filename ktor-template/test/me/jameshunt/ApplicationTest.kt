package me.jameshunt

import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*
import me.jameshunt.app.module

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/users").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }
}
