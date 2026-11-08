package app.roleparty.security.test

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.security.domain.port.secondary.Accounts
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

abstract class AccountsContractTest(createAccounts: () -> Accounts) : FixturedBehaviorTest() {

    private val accounts = createAccounts()

    private val email = EmailAddress.from("gimli@roleparty.app")
    private val otherEmail = EmailAddress.from("legolas@roleparty.app")

    init {
        Given("an email address registered once") {
            val accountId = accounts.registerOrFind(email)

            When("registering or finding it again") {
                val again = accounts.registerOrFind(email)

                Then("it should give the same account") {
                    again shouldBe accountId
                }
            }

            When("registering or finding another address") {
                val other = accounts.registerOrFind(otherEmail)

                Then("it should give another account") {
                    other shouldNotBe accountId
                }
            }
        }
    }
}
