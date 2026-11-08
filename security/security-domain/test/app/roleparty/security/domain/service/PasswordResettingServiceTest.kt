package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.test.SecurityBehaviorTest
import io.kotest.matchers.collections.shouldBeEmpty
import java.util.Locale
import kotlin.time.Duration.Companion.minutes

class PasswordResettingServiceTest : SecurityBehaviorTest() {

    private val resetPassword = fixtures.resetPassword
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val password = "a-valid-password"
    private val newPassword = "another-valid-password"
    private val en = Locale.ENGLISH

    init {
        Given("a user who forgot their password") {
            val refreshToken = fixtures.`given a signed in user`(email, password)
            resetPassword.request(email, Locale.FRENCH)
            val code = fixtures.lastCodeSentTo(email)

            When("resetting it with the code") {
                val outcome = resetPassword.confirm(email, code, newPassword)

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("the new password should replace the old one") {
                    fixtures.signIn(email, newPassword).shouldBeSuccess()
                    fixtures.signIn(email, password) shouldBeFailure SecurityError.InvalidCredentials
                }

                Then("existing sessions should be revoked") {
                    fixtures.refreshAuthentication(refreshToken) shouldBeFailure SecurityError.InvalidRefreshToken
                }

                Then("the code should be consumed") {
                    resetPassword.confirm(email, code, password) shouldBeFailure SecurityError.InvalidEmailCode
                }
            }
        }

        Given("a reset request for an unknown address") {
            When("requesting it") {
                resetPassword.request(email, en)

                Then("nothing should be sent") {
                    fixtures.emailsSentTo(email).shouldBeEmpty()
                }
            }
        }

        Given("a reset code and an invalid new password") {
            fixtures.`given a verified user`(email, password)
            resetPassword.request(email, en)
            val code = fixtures.lastCodeSentTo(email)

            When("resetting with it") {
                val outcome = resetPassword.confirm(email, code, "short")

                Then("it should be rejected and the code kept") {
                    outcome shouldBeFailure SecurityError.InvalidPassword
                    resetPassword.confirm(email, code, newPassword).shouldBeSuccess()
                }
            }
        }

        Given("a reset code that expired") {
            fixtures.`given a verified user`(email, password)
            resetPassword.request(email, en)
            val code = fixtures.lastCodeSentTo(email)
            clock.advanceBy(16.minutes)

            When("resetting with it") {
                val outcome = resetPassword.confirm(email, code, newPassword)

                Then("it should fail") {
                    outcome shouldBeFailure SecurityError.InvalidEmailCode
                }
            }
        }

        Given("a sign up code used as a reset code") {
            fixtures.signUp(email, password, en).shouldBeSuccess()
            val code = fixtures.lastCodeSentTo(email)

            When("resetting with it") {
                val outcome = resetPassword.confirm(email, code, newPassword)

                Then("it should fail") {
                    outcome shouldBeFailure SecurityError.InvalidEmailCode
                }
            }
        }
    }
}
