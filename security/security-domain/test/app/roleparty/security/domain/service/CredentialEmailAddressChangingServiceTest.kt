package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.test.SecurityBehaviorTest

class CredentialEmailAddressChangingServiceTest : SecurityBehaviorTest() {

    private val changeCredentialEmailAddress = fixtures.changeCredentialEmailAddress
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val password = "a-valid-password"

    init {
        Given("a user whose account changed its email address") {
            fixtures.`given a verified user`(email, password)
            val newEmail = EmailAddress.from("gimli.son.of.gloin@roleparty.app")
            changeCredentialEmailAddress(fixtures.accountOf(email), newEmail)

            When("signing in") {
                Then("only the new address should work") {
                    fixtures.signIn(newEmail, password).shouldBeSuccess()
                    fixtures.signIn(email, password) shouldBeFailure SecurityError.InvalidCredentials
                }
            }
        }
    }
}
