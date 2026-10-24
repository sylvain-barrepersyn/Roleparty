package app.roleparty.application.backend

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(CallLogging)

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}