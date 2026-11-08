package app.roleparty.core.ktor.rest

import app.roleparty.core.commons.Outcome
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.install
import io.ktor.server.resources.get as getResource
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable

class KtorRestTest : FunSpec({

    val errors = ErrorResponder<String> { call, error -> call.respond(HttpStatusCode.Conflict, error) }

    fun ApplicationTestBuilder.routes() = application {
        resourceRouting {
            getResource<Numbers.Number> { resource -> call.respondText("${resource.number * 2}") }
            get("/outcome/{success}") {
                val outcome: Outcome<Int, String> = if (call.parameters["success"] == "true") Outcome.Success(21) else Outcome.Failure("refused")
                call.respondOutcome(outcome, errors, HttpStatusCode.Created) { "${it * 2}" }
            }
            get("/status/{success}") {
                val outcome: Outcome<Unit, String> = if (call.parameters["success"] == "true") Outcome.success() else Outcome.Failure("refused")
                call.respondStatus(outcome, errors)
            }
            route("/limited") {
                install(BodyLimit) { maxBytes = 4 }
                post { call.respondText("accepted") }
            }
            post("/bytes") {
                val bytes = call.receiveBytes(4)
                if (bytes == null) call.respond(HttpStatusCode.PayloadTooLarge) else call.respondText("${bytes.size}")
            }
        }
    }

    test("a resource should be decoded from the path, or the request refused") {
        testApplication {
            routes()

            client.get("/numbers/21").bodyAsText() shouldBe "42"
            client.get("/numbers/twenty").status shouldBe HttpStatusCode.BadRequest
        }
    }

    test("an outcome should be answered with its value or its error") {
        testApplication {
            routes()

            client.get("/outcome/true").run { status to bodyAsText() } shouldBe (HttpStatusCode.Created to "42")
            client.get("/outcome/false").run { status to bodyAsText() } shouldBe (HttpStatusCode.Conflict to "refused")
            client.get("/status/true").status shouldBe HttpStatusCode.NoContent
            client.get("/status/false").status shouldBe HttpStatusCode.Conflict
        }
    }

    test("a body over the limit should be refused") {
        testApplication {
            routes()

            client.post("/limited") { setBody("1234") }.status shouldBe HttpStatusCode.OK
            client.post("/limited") { setBody("12345") }.status shouldBe HttpStatusCode.PayloadTooLarge
            client.post("/bytes") { setBody("1234") }.bodyAsText() shouldBe "4"
            client.post("/bytes") { setBody("12345") }.status shouldBe HttpStatusCode.PayloadTooLarge
        }
    }
})

@Serializable
@Resource("/numbers")
private class Numbers {

    @Serializable
    @Resource("{number}")
    class Number(val number: Int, val parent: Numbers = Numbers())
}
