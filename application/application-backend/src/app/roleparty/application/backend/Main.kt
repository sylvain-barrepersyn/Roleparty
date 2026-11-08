package app.roleparty.application.backend

import app.roleparty.account.infrastructure.loadAccountModule
import app.roleparty.account.infrastructure.serveAccountRpc
import app.roleparty.application.backend.email.loadEmailSender
import app.roleparty.application.backend.error.LocalizedAgenda
import app.roleparty.application.backend.error.LocalizedCampaign
import app.roleparty.application.backend.error.LocalizedCreature
import app.roleparty.application.backend.error.LocalizedLibrary
import app.roleparty.application.backend.error.LocalizedMedia
import app.roleparty.application.backend.error.LocalizedSecurity
import app.roleparty.application.backend.error.LocalizedStory
import app.roleparty.campaign.agenda.infrastructure.loadAgendaModule
import app.roleparty.campaign.agenda.infrastructure.serveAgendaRoutes
import app.roleparty.campaign.creature.infrastructure.loadCreatureModule
import app.roleparty.campaign.creature.infrastructure.serveCreatureRoutes
import app.roleparty.campaign.infrastructure.loadCampaignModule
import app.roleparty.campaign.infrastructure.serveCampaignRoutes
import app.roleparty.campaign.library.infrastructure.loadLibraryModule
import app.roleparty.campaign.library.infrastructure.serveLibraryRoutes
import app.roleparty.campaign.story.infrastructure.loadStoryModule
import app.roleparty.campaign.story.infrastructure.serveStoryRoutes
import app.roleparty.communication.commons.Email
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.communication.commons.EmailSender
import app.roleparty.core.commons.valueOrFile
import app.roleparty.core.database.loadPostgres
import app.roleparty.core.outbox.loadOutbox
import app.roleparty.core.rabbitmq.RabbitMq
import app.roleparty.core.rabbitmq.loadRabbitMq
import app.roleparty.core.rabbitmq.monitorDeadLetters
import app.roleparty.media.infrastructure.loadMediaModule
import app.roleparty.media.infrastructure.serveMediaRoutes
import app.roleparty.media.infrastructure.serveMediaRpc
import app.roleparty.security.infrastructure.loadSecurity
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val TRUST_PROXY = "TRUST_PROXY"
private const val ALERT_EMAIL_ADDRESS = "ALERT_EMAIL_ADDRESS"

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() = backend(System.getenv(), loadEmailSender(System.getenv()))

fun Application.backend(env: Map<String, String>, emailSender: EmailSender) {
    install(CallLogging)
    install(ContentNegotiation) { json() }
    if (env[TRUST_PROXY] == "true") install(XForwardedHeaders) { useLastProxy() }

    val postgres = loadPostgres(env)
    val rabbitMq = loadRabbitMq(env)

    serveAccountRpc(loadAccountModule(postgres), env)
    loadSecurity(postgres, rabbitMq, emailSender, LocalizedSecurity, env)

    val media = loadMediaModule(postgres)
    serveMediaRoutes(media, LocalizedMedia)
    serveMediaRpc(media, env)

    serveCampaignRoutes(loadCampaignModule(postgres), LocalizedCampaign)
    serveStoryRoutes(loadStoryModule(postgres), LocalizedStory)
    serveAgendaRoutes(loadAgendaModule(postgres), LocalizedAgenda)
    serveCreatureRoutes(loadCreatureModule(postgres), LocalizedCreature)
    serveLibraryRoutes(loadLibraryModule(postgres), LocalizedLibrary)

    loadOutbox(postgres, rabbitMq)
    alertOnDeadLetters(rabbitMq, emailSender, env)

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}

private fun Application.alertOnDeadLetters(rabbitMq: RabbitMq, emailSender: EmailSender, env: Map<String, String>) {
    val recipient = env.valueOrFile(ALERT_EMAIL_ADDRESS)?.let(EmailAddress::from)

    monitorDeadLetters(rabbitMq) { queue, messages ->
        if (recipient != null) {
            val description = "The dead-letter queue $queue holds $messages message(s) that could not be handled."
            emailSender.send(Email(recipient, "Roleparty: dead letters in $queue", description, "<p>$description</p>"))
        }
    }
}
