package me.jameshunt

import io.ktor.http.*
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Test
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document

class UserApiDocTest: AsciiDocBaseTest() {

    @Test
    fun `generate user docs`() {
        given(this.spec)
            .filter(document("users"))
            .`when`()
            .port(8080)
            .get("/users")
            .then()
            .assertThat()
            .statusCode(200)
            .header(HttpHeaders.ContentType, "application/json; charset=UTF-8")
    }
}