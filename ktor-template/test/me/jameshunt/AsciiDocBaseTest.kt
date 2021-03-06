package me.jameshunt

import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.jameshunt.app.module
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation


@ExtendWith(RestDocumentationExtension::class, AsciiDocExtension::class)
open class AsciiDocBaseTest {

    lateinit var spec: RequestSpecification

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        this.spec = createSpecBuilder(restDocumentation).build()
    }

    open fun createSpecBuilder(restDocumentation: RestDocumentationContextProvider): RequestSpecBuilder {
        return restDocumentation.defaultSpecBuilder()
    }
}

class AsciiDocExtension : BeforeAllCallback, CloseableResource {

    private lateinit var server: NettyApplicationEngine

    override fun beforeAll(context: ExtensionContext) {
        if (!started) {
            started = true
            // Your "before all tests" startup logic goes here

            val env = applicationEngineEnvironment {
                module {
                    module(true)
                }
                // Public API
                connector {
                    host = "0.0.0.0"
                    port = 8080
                }
            }
            server = embeddedServer(Netty, env).start(false)


            // The following line registers a callback hook when the root test context is shut down
            context.root.getStore(ExtensionContext.Namespace.GLOBAL).put("asciidoc embedded server", this)
        }
    }

    override fun close() {
        // Your "after all tests" logic goes here
        server.stop(1000, 10000)
    }

    companion object {
        private var started = false
    }
}

fun RestDocumentationContextProvider.defaultSpecBuilder(): RequestSpecBuilder = RequestSpecBuilder()
    .addFilter(
        RestAssuredRestDocumentation.documentationConfiguration(this)
            .operationPreprocessors()
            .withRequestDefaults(
                Preprocessors.prettyPrint()
            )
            .withResponseDefaults(
                Preprocessors.prettyPrint()
            )
            .and()
    )