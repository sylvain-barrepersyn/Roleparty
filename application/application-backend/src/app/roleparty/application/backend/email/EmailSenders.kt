package app.roleparty.application.backend.email

import app.roleparty.communication.commons.EmailSender
import app.roleparty.communication.hostinger.hostingerEmailSender
import io.ktor.server.application.Application

private const val EMAIL_SENDER = "EMAIL_SENDER"
private const val LOG = "log"

fun Application.loadEmailSender(env: Map<String, String>): EmailSender =
    if (env[EMAIL_SENDER] == LOG) {
        environment.log.warn("Emails are logged instead of being sent: never use $EMAIL_SENDER=$LOG in production")
        EmailSender { email -> environment.log.info("Email to ${email.to}: ${email.subject}\n${email.text}") }
    } else {
        hostingerEmailSender(env)
    }
