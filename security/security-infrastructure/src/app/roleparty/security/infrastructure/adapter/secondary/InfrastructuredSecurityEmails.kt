package app.roleparty.security.infrastructure.adapter.secondary

import app.roleparty.communication.commons.Email
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.communication.commons.EmailSender
import app.roleparty.security.domain.port.secondary.SecurityEmails
import java.util.Locale
import java.util.ResourceBundle
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.html
import kotlinx.html.p
import kotlinx.html.stream.appendHTML
import kotlinx.html.strong
import org.slf4j.Logger

private const val BUNDLE = "i18n/security-emails"

internal class InfrastructuredSecurityEmails(
    private val scope: CoroutineScope,
    private val sender: EmailSender,
    private val log: Logger,
) : SecurityEmails {

    override fun verificationCode(to: EmailAddress, code: String, locale: Locale) = send(to, "verification", locale, code)

    override fun alreadyRegistered(to: EmailAddress, locale: Locale) = send(to, "already-registered", locale, code = null)

    override fun passwordResetCode(to: EmailAddress, code: String, locale: Locale) = send(to, "password-reset", locale, code)

    private fun send(to: EmailAddress, template: String, locale: Locale, code: String?) {
        val email = email(to, template, locale, code)
        scope.launch {
            try {
                sender.send(email)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                log.error("Impossible to send the $template email", exception)
            }
        }
    }

    companion object {
        internal fun email(to: EmailAddress, template: String, locale: Locale, code: String?): Email {
            val messages = ResourceBundle.getBundle(BUNDLE, locale, ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES))
            val title = messages.getString("$template.title")
            val body = messages.getString("$template.body")
            val ignore = messages.getString("$template.ignore")

            return Email(
                to = to,
                subject = messages.getString("$template.subject"),
                text = listOfNotNull(title, body, code, ignore).joinToString("\n\n"),
                html = buildString {
                    appendHTML().html {
                        body {
                            h1 { +title }
                            p { +body }
                            code?.let { p { strong { +it } } }
                            p { +ignore }
                        }
                    }
                },
            )
        }
    }
}
