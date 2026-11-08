package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.test.SecurityBehaviorTest
import app.roleparty.security.test.SentEmail
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.Locale
import kotlin.time.Duration.Companion.minutes

class SigningUpServiceTest : SecurityBehaviorTest() {

    private val signUp = fixtures.signUp
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val password = "a-valid-password"
    private val newPassword = "another-valid-password"
    private val en = Locale.ENGLISH

    init {
        Given("an unused email address") {
            When("signing up with a valid password") {
                val outcome = signUp(email, password, Locale.FRENCH)

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("it should send a code in the language of the user") {
                    fixtures.emailsSentTo(email).single().shouldBeInstanceOf<SentEmail.Verification>().locale shouldBe Locale.FRENCH
                }

                Then("it should not create anything before the address is verified") {
                    fixtures.registeredAccounts() shouldBe 0
                    fixtures.credentialOf(email).shouldBeNull()
                    fixtures.signIn(email, password) shouldBeFailure SecurityError.InvalidCredentials
                }
            }
        }

        Given("a password of an invalid length") {
            When("signing up with it") {
                val tooShort = signUp(email, "short", en)
                val tooLong = signUp(email, "x".repeat(129), en)

                Then("it should be rejected") {
                    tooShort shouldBeFailure SecurityError.InvalidPassword
                    tooLong shouldBeFailure SecurityError.InvalidPassword
                }

                Then("nothing should be sent") {
                    fixtures.emailsSentTo(email).shouldBeEmpty()
                }
            }
        }

        Given("a verified user") {
            fixtures.`given a verified user`(email, password)
            val credential = fixtures.credentialOf(email)

            When("someone signs up again with their address") {
                val outcome = signUp(email, newPassword, en)

                Then("it should look like a success, to hide that the address is used") {
                    outcome.shouldBeSuccess()
                }

                Then("it should warn the owner of the address instead of sending a code") {
                    fixtures.emailsSentTo(email).last().shouldBeInstanceOf<SentEmail.AlreadyRegistered>()
                }

                Then("it should not change their password") {
                    fixtures.credentialOf(email) shouldBe credential
                }
            }
        }

        Given("an address receiving many sign ups") {
            When("signing up repeatedly") {
                val outcomes = List(6) { signUp(email, password, en) }

                Then("it should always look like a success") {
                    outcomes.forEach { it.shouldBeSuccess() }
                }

                Then("it should only send a limited number of emails") {
                    fixtures.emailsSentTo(email) shouldHaveSize 3
                }
            }
        }

        Given("a limit of emails reached an hour ago") {
            repeat(3) { signUp(email, password, en) }
            clock.advanceBy(61.minutes)

            When("signing up again") {
                signUp(email, password, en).shouldBeSuccess()

                Then("an email should be sent again") {
                    fixtures.emailsSentTo(email) shouldHaveSize 4
                }
            }
        }

        Given("a code stored on sign up") {
            signUp(email, password, en).shouldBeSuccess()

            When("looking at the credential") {
                Then("no code should be readable without the email") {
                    fixtures.credentialOf(email).shouldBeNull()
                    fixtures.storedEmailCodes() shouldBe 1
                    fixtures.lastCodeSentTo(email).shouldNotBeNull()
                }
            }
        }
    }
}
