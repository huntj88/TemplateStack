package me.jameshunt

import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import org.junit.jupiter.api.Test
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document

class GitHubApiTest : AsciiDocBaseTest() {

    override fun createSpecBuilder(restDocumentation: RestDocumentationContextProvider): RequestSpecBuilder {
        return super.createSpecBuilder(restDocumentation).setBaseUri("https://api.github.com")
    }

    /**
     * This test accesses api.github.com and thus only works with an Internet connection and if api.github.com.
     * Such a test is usually avoided, but is used here to demo that any REST API could be documented with
     * Spring REST Docs.
     */
    @Test
    fun githubUserTest() {
        given(spec)
            .filter(
                document(
                    "github/get-user",
                    preprocessResponse(prettyPrint()),
                    relaxedResponseFields(
                        fieldWithPath("name").description("Name of the user"),
                        fieldWithPath("public_repos").description("Number of public repos")
                    )
                )
            )
            .`when`()
            .get("/users/huntj88")
            .then()
            .statusCode(200)
    }
}