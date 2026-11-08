package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.test.AccountBehaviorTest
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class AccountEmailAddressChangingServiceTest : AccountBehaviorTest() {

    private val changeAccountEmailAddress = fixtures.changeAccountEmailAddress
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val otherEmail = EmailAddress.from("legolas@roleparty.app")

    init {
        Given("a registered account and an unused email address") {
            val accountId = fixtures.`given an account`(email)

            When("changing to this email address") {
                val outcome = changeAccountEmailAddress(accountId, otherEmail)

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("the account should use the new email address") {
                    fixtures.accountOf(accountId).emailAddress shouldBe otherEmail
                }
            }
        }

        Given("a registered account") {
            val accountId = fixtures.`given an account`(email)
            val writes = fixtures.writes()

            When("changing to its current email address") {
                val outcome = changeAccountEmailAddress(accountId, email)

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("nothing should be written, so that no event is published") {
                    fixtures.writes() shouldBe writes
                }
            }
        }

        Given("two registered accounts") {
            val accountId = fixtures.`given an account`(email)
            fixtures.`given an account`(otherEmail)

            When("one account changes to the email address of the other") {
                val outcome = changeAccountEmailAddress(accountId, otherEmail)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.EmailAddressAlreadyUsed(otherEmail)
                }

                Then("the account should keep its email address") {
                    fixtures.accountOf(accountId).emailAddress shouldBe email
                }
            }
        }

        Given("an unknown account") {
            val accountId = AccountId.generate()

            When("changing its email address") {
                val outcome = changeAccountEmailAddress(accountId, email)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AccountNotFound(accountId)
                }
            }
        }
    }
}
