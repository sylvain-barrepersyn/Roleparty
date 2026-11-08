package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.test.AccountBehaviorTest
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class AccountRegisteringServiceTest : AccountBehaviorTest() {

    private val registerAccount = fixtures.registerAccount
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val otherEmail = EmailAddress.from("legolas@roleparty.app")

    init {
        Given("an unused email address") {
            When("registering with it") {
                val accountId = registerAccount(email).shouldBeSuccess()

                Then("the account should use this email address") {
                    fixtures.accountOf(accountId).emailAddress shouldBe email
                }

                Then("the account should be a simple user") {
                    fixtures.accountOf(accountId).role shouldBe AccountRole.USER
                }
            }
        }

        Given("an email address used by an account") {
            fixtures.`given an account`(email)

            When("registering with it") {
                val outcome = registerAccount(email)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.EmailAddressAlreadyUsed(email)
                }
            }
        }

        Given("an email address released by an account") {
            val previousOwner = fixtures.`given an account`(email)
            fixtures.changeAccountEmailAddress(previousOwner, otherEmail).shouldBeSuccess()

            When("registering with it") {
                val outcome = registerAccount(email)

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }
            }
        }
    }
}
