package me.jameshunt

import io.ktor.http.*
import io.restassured.RestAssured.given
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document

@ExtendWith(RestDocumentationExtension::class, AsciiDocExtension::class)
class UserApiDocTest {

    lateinit var spec: RequestSpecification
    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        this.spec = restDocumentation.defaultSpecBuilder().build()
    }

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