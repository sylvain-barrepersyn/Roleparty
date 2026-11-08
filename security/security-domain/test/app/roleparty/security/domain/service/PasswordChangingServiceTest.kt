package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.test.SecurityBehaviorTest

class PasswordChangingServiceTest : SecurityBehaviorTest() {

    private val changePassword = fixtures.changePassword
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val password = "a-valid-password"
    private val newPassword = "another-valid-password"

    init {
        Given("a signed in user changing their password") {
            val refreshToken = fixtures.`given a signed in user`(email, password)

            When("giving their current password") {
                changePassword(fixtures.accountOf(email), password, newPassword).shouldBeSuccess()

                Then("the new password should replace the old one") {
                    fixtures.signIn(email, newPassword).shouldBeSuccess()
                    fixtures.signIn(email, password) shouldBeFailure SecurityError.InvalidCredentials
                }

                Then("existing sessions should be revoked") {
                    fixtures.refreshAuthentication(refreshToken) shouldBeFailure SecurityError.InvalidRefreshToken
                }
            }
        }

        Given("a signed in user giving a wrong current password") {
            fixtures.`given a verified user`(email, password)
            val accountId = fixtures.accountOf(email)

            When("changing their password") {
                val outcome = changePassword(accountId, "wrong-password", newPassword)

                Then("it should fail and keep the password") {
                    outcome shouldBeFailure SecurityError.InvalidCredentials
                    fixtures.signIn(email, password).shouldBeSuccess()
                }
            }
        }
    }
}
