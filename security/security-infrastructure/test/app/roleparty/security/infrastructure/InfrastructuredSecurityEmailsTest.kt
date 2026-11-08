package app.roleparty.security.infrastructure

import app.roleparty.communication.commons.Email
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredSecurityEmails
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.util.Locale
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.slf4j.LoggerFactory

class InfrastructuredSecurityEmailsTest : FunSpec({
    val to = EmailAddress.from("gimli@roleparty.app")

    test("every email should exist in every language, with its code in the text and in the html") {
        listOf("verification", "password-reset").forEach { template ->
            val english = InfrastructuredSecurityEmails.email(to, template, Locale.ENGLISH, "ABCD2345")
            val french = InfrastructuredSecurityEmails.email(to, template, Locale.FRENCH, "ABCD2345")

            english.subject shouldNotBe french.subject
            listOf(english, french).forEach { email ->
                email.to shouldBe to
                email.text shouldContain "ABCD2345"
                email.html shouldContain "ABCD2345"
            }
        }
    }

    test("the already registered email should not hold any code and exist in every language") {
        val english = InfrastructuredSecurityEmails.email(to, "already-registered", Locale.ENGLISH, code = null)
        val french = InfrastructuredSecurityEmails.email(to, "already-registered", Locale.FRENCH, code = null)

        english.subject shouldNotBe french.subject
        english.text shouldNotContain "null"
    }

    test("a failure of the sender should not reach the caller") {
        val attempted = CompletableDeferred<Email>()
        val emails = InfrastructuredSecurityEmails(
            scope = CoroutineScope(Dispatchers.Default),
            sender = { email ->
                attempted.complete(email)
                error("The provider is down")
            },
            log = LoggerFactory.getLogger("test"),
        )

        emails.verificationCode(to, "ABCD2345", Locale.ENGLISH)

        attempted.await().to shouldBe to
    }
})
