package app.roleparty.account.test

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.Account
import app.roleparty.account.domain.model.AccountError.EmailAddressAlreadyUsed
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

abstract class AccountsContractTest(createAccounts: () -> Accounts) : FixturedBehaviorTest() {

    private val accounts = createAccounts()

    private val firstEmail = EmailAddress.from("first@roleparty.app")
    private val secondEmail = EmailAddress.from("second@roleparty.app")

    private suspend fun givenAccount(emailAddress: EmailAddress): Account {
        val account = Account(AccountId.generate(), emailAddress, AccountRole.USER)
        accounts.register(account).shouldBeSuccess()
        return account
    }

    init {
        Given("an unknown account") {
            val accountId = AccountId.generate()

            When("finding it") {
                Then("it should find nothing") {
                    accounts.findById(accountId).shouldBeNull()
                    accounts.findByEmailAddress(firstEmail).shouldBeNull()
                }
            }

            When("changing it") {
                accounts.changeEmailAddress(accountId, firstEmail).shouldBeSuccess()
                accounts.changeRole(accountId, AccountRole.ADMIN)

                Then("it should not create it") {
                    accounts.findById(accountId).shouldBeNull()
                    accounts.findByEmailAddress(firstEmail).shouldBeNull()
                }
            }
        }

        Given("a registered account") {
            val account = givenAccount(firstEmail)

            When("finding it") {
                Then("it should be found by id and by email address") {
                    accounts.findById(account.id) shouldBe account
                    accounts.findByEmailAddress(firstEmail) shouldBe account
                }
            }
        }

        Given("an account that changed its email address") {
            val account = givenAccount(firstEmail)
            accounts.changeEmailAddress(account.id, secondEmail).shouldBeSuccess()
            val expected = account.copy(emailAddress = secondEmail)

            When("finding it") {
                Then("it should be found with the new address only") {
                    accounts.findById(account.id) shouldBe expected
                    accounts.findByEmailAddress(secondEmail) shouldBe expected
                    accounts.findByEmailAddress(firstEmail).shouldBeNull()
                }
            }
        }

        Given("an account that changed its role") {
            val account = givenAccount(firstEmail)
            accounts.changeRole(account.id, AccountRole.ADMIN)

            When("finding it") {
                Then("it should have the new role") {
                    accounts.findById(account.id) shouldBe account.copy(role = AccountRole.ADMIN)
                }
            }
        }

        Given("an email address held by an account") {
            givenAccount(firstEmail)
            val other = Account(AccountId.generate(), firstEmail, AccountRole.USER)

            When("registering another account with it") {
                val outcome = accounts.register(other)

                Then("it should report the email address as used") {
                    outcome shouldBeFailure EmailAddressAlreadyUsed(firstEmail)
                }

                Then("the other account should not be created") {
                    accounts.findById(other.id).shouldBeNull()
                }
            }
        }

        Given("two accounts with different email addresses") {
            val account = givenAccount(firstEmail)
            givenAccount(secondEmail)

            When("one account changes to the email address of the other") {
                val outcome = accounts.changeEmailAddress(account.id, secondEmail)

                Then("it should report the email address as used") {
                    outcome shouldBeFailure EmailAddressAlreadyUsed(secondEmail)
                }

                Then("the account should not change") {
                    accounts.findById(account.id) shouldBe account
                }
            }
        }

        Given("an email address released by an account") {
            val previousOwner = givenAccount(firstEmail)
            accounts.changeEmailAddress(previousOwner.id, secondEmail).shouldBeSuccess()

            When("registering another account with it") {
                val outcome = accounts.register(Account(AccountId.generate(), firstEmail, AccountRole.USER))

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }
            }
        }
    }
}
