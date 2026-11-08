package app.roleparty.core.ktor.rest

import app.roleparty.core.commons.Outcome
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

fun interface ErrorResponder<in E> {

    suspend fun respond(call: ApplicationCall, error: E)
}

suspend fun <T, E> ApplicationCall.respondOutcome(
    outcome: Outcome<T, E>,
    errors: ErrorResponder<E>,
    status: HttpStatusCode = HttpStatusCode.OK,
    body: suspend (T) -> Any,
) {
    when (outcome) {
        is Outcome.Failure -> errors.respond(this, outcome.error)
        is Outcome.Success -> respond(status, body(outcome.value))
    }
}

suspend fun <E> ApplicationCall.respondStatus(
    outcome: Outcome<*, E>,
    errors: ErrorResponder<E>,
    status: HttpStatusCode = HttpStatusCode.NoContent,
) {
    when (outcome) {
        is Outcome.Failure -> errors.respond(this, outcome.error)
        is Outcome.Success -> respond(status)
    }
}
