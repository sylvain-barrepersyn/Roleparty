package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.test.SecurityBehaviorTest
import io.kotest.matchers.shouldBe
import java.util.Locale
import kotlin.time.Duration.Companion.minutes

class EmailAddressVerifyingServiceTest : SecurityBehaviorTest() {

    private val verifyEmailAddress = fixtures.verifyEmailAddress
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val password = "a-valid-password"
    private val en = Locale.ENGLISH

    init {
        Given("a sign up waiting for verification") {
            fixtures.signUp(email, password, en).shouldBeSuccess()
            val code = fixtures.lastCodeSentTo(email)

            When("verifying with the code, in any case and with spaces") {
                val outcome = verifyEmailAddress(email, " ${code.lowercase()} ")

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("the user should be able to sign in") {
                    fixtures.signIn(email, password).shouldBeSuccess()
                }

                Then("the code should be consumed") {
                    fixtures.storedEmailCodes() shouldBe 0
                    verifyEmailAddress(email, code) shouldBeFailure SecurityError.InvalidEmailCode
                }
            }
        }

        Given("a sign up and a wrong code") {
            fixtures.signUp(email, password, en).shouldBeSuccess()

            When("verifying with it") {
                val outcome = verifyEmailAddress(email, "WRONG")

                Then("it should fail") {
                    outcome shouldBeFailure SecurityError.InvalidEmailCode
                }

                Then("nothing should be created") {
                    fixtures.registeredAccounts() shouldBe 0
                }
            }
        }

        Given("a sign up whose code expired") {
            fixtures.signUp(email, password, en).shouldBeSuccess()
            val code = fixtures.lastCodeSentTo(email)
            clock.advanceBy(61.minutes)

            When("verifying with it") {
                val outcome = verifyEmailAddress(email, code)

                Then("it should fail") {
                    outcome shouldBeFailure SecurityError.InvalidEmailCode
                }
            }
        }

        Given("an attacker who signed up first with the address of a victim") {
            fixtures.signUp(email, "the-attacker-password", en).shouldBeSuccess()
            fixtures.signUp(email, "the-victim-password", en).shouldBeSuccess()
            val victimCode = fixtures.lastCodeSentTo(email)

            When("the victim verifies their own code") {
                verifyEmailAddress(email, victimCode).shouldBeSuccess()

                Then("the victim's password should work") {
                    fixtures.signIn(email, "the-victim-password").shouldBeSuccess()
                }

                Then("the attacker's password should not") {
                    fixtures.signIn(email, "the-attacker-password") shouldBeFailure SecurityError.InvalidCredentials
                }
            }
        }

        Given("a wrong code tried too many times") {
            fixtures.signUp(email, password, en).shouldBeSuccess()
            val code = fixtures.lastCodeSentTo(email)
            repeat(10) { verifyEmailAddress(email, "WRONG") }

            When("trying the right code") {
                val outcome = verifyEmailAddress(email, code)

                Then("it should be blocked") {
                    outcome shouldBeFailure SecurityError.TooManyAttempts
                }
            }
        }
    }
}
