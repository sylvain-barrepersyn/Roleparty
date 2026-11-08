package app.roleparty.security.test

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.security.domain.port.secondary.SecurityEmails
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

class SecurityEmailsContractSubject(
    val emails: SecurityEmails,
    val deliveredTo: (EmailAddress) -> List<String>,
)

abstract class SecurityEmailsContractTest(createSubject: () -> SecurityEmailsContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()
    private val emails = subject.emails

    private val email = EmailAddress.from("gimli@roleparty.app")
    private val otherEmail = EmailAddress.from("legolas@roleparty.app")
    private val code = "ABCD2345"

    private suspend fun delivered(to: EmailAddress, count: Int): List<String> =
        eventually(5.seconds) { subject.deliveredTo(to).also { it shouldHaveSize count } }

    init {
        Given("a verification code") {
            When("sending it") {
                emails.verificationCode(email, code, Locale.ENGLISH)

                Then("only its recipient should receive it, with the code") {
                    delivered(email, 1).single() shouldContain code
                    subject.deliveredTo(otherEmail).shouldBeEmpty()
                }
            }
        }

        Given("a password reset code") {
            When("sending it") {
                emails.passwordResetCode(email, code, Locale.FRENCH)

                Then("only its recipient should receive it, with the code") {
                    delivered(email, 1).single() shouldContain code
                    subject.deliveredTo(otherEmail).shouldBeEmpty()
                }
            }
        }

        Given("an address already registered") {
            When("warning its owner") {
                emails.alreadyRegistered(email, Locale.ENGLISH)

                Then("only its owner should receive one email") {
                    delivered(email, 1)
                    subject.deliveredTo(otherEmail).shouldBeEmpty()
                }
            }
        }
    }
}
