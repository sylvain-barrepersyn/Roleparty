package app.roleparty.core.ktor.rest

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.resources.Resources
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing

fun Application.resourceRouting(configuration: Route.() -> Unit) {
    pluginOrNull(Resources) ?: install(Resources)
    routing(configuration)
}
