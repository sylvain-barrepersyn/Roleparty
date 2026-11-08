package app.roleparty.account.infrastructure

import app.roleparty.account.infrastructure.adapter.primary.AccountRpcController
import app.roleparty.account.test.AccountBehaviorTest
import app.roleparty.communication.commons.EmailAddress
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class AccountRpcControllerTest : AccountBehaviorTest() {

    private val controller = AccountRpcController(fixtures.registerAccount, fixtures.findAccount)
    private val email = EmailAddress.from("gimli@roleparty.app")

    init {
        Given("an unused email address") {
            When("registering or finding an account with it") {
                val accountId = controller.registerOrFind(email)

                Then("the account should be registered") {
                    fixtures.accountOf(accountId).emailAddress shouldBe email
                }
            }
        }

        Given("an email address held by an account") {
            val owner = fixtures.`given an account`(email)

            When("registering or finding an account with it") {
                val accountId = controller.registerOrFind(email)

                Then("it should return the existing account") {
                    accountId shouldBe owner
                }
            }

            When("registering or finding an account with another address") {
                val accountId = controller.registerOrFind(EmailAddress.from("legolas@roleparty.app"))

                Then("it should register another account") {
                    accountId shouldNotBe owner
                }
            }
        }
    }
}
