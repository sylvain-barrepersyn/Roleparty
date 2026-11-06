package app.roleparty.account.test

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.AccountEvent
import app.roleparty.account.domain.model.AccountEvent.Payload
import app.roleparty.account.domain.model.AccountOverview
import app.roleparty.account.domain.model.AccountVersion
import app.roleparty.account.domain.port.secondary.AccountAppendFailure
import app.roleparty.account.domain.port.secondary.AccountOverviews
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

data class AccountsContractSubject(val accounts: Accounts, val accountOverviews: AccountOverviews)

abstract class AccountsContractTest(createSubject: () -> AccountsContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()
    private val accounts = subject.accounts
    private val accountOverviews = subject.accountOverviews

    private val firstEmail = EmailAddress.from("first@roleparty.app")
    private val secondEmail = EmailAddress.from("second@roleparty.app")

    private fun event(accountId: AccountId, payload: Payload) =
        AccountEvent(AccountEvent.Id.generate(), accountId, clock.now(), payload)

    private fun registration(accountId: AccountId, emailAddress: EmailAddress) =
        event(accountId, Payload.Registered(emailAddress, AccountRole.USER))

    private suspend fun givenRegisteredAccount(emailAddress: EmailAddress): AccountId {
        val accountId = AccountId.generate()
        accounts.append(accountId, AccountVersion.INITIAL, listOf(registration(accountId, emailAddress))).shouldBeSuccess()
        return accountId
    }

    init {
        Given("an unknown account") {
            val accountId = AccountId.generate()

            When("loading it") {
                val history = accounts.load(accountId)

                Then("its history should be empty") {
                    history.events.shouldBeEmpty()
                }

                Then("its version should be the initial one") {
                    history.version shouldBe AccountVersion.INITIAL
                }
            }

            When("finding its overview by account id") {
                val overview = accountOverviews.findById(accountId)

                Then("it should find nothing") {
                    overview.shouldBeNull()
                }
            }

            When("finding an overview by an unused email address") {
                val overview = accountOverviews.findByEmailAddress(firstEmail)

                Then("it should find nothing") {
                    overview.shouldBeNull()
                }
            }
        }

        Given("a freshly registered account") {
            val accountId = givenRegisteredAccount(firstEmail)
            val expected = AccountOverview.from(accounts.load(accountId).events)

            When("finding its overview by account id") {
                val overview = accountOverviews.findById(accountId)

                Then("it should be up to date") {
                    overview shouldBe expected
                }
            }

            When("finding its overview by email address") {
                val overview = accountOverviews.findByEmailAddress(firstEmail)

                Then("it should be up to date") {
                    overview shouldBe expected
                }
            }
        }

        Given("an account that just changed its email address") {
            val accountId = givenRegisteredAccount(firstEmail)
            accounts.append(
                accountId,
                accounts.load(accountId).version,
                listOf(event(accountId, Payload.EmailAddressChanged(secondEmail))),
            ).shouldBeSuccess()
            val expected = AccountOverview.from(accounts.load(accountId).events)

            When("finding its overview by account id") {
                val overview = accountOverviews.findById(accountId)

                Then("it should be up to date") {
                    overview shouldBe expected
                }
            }

            When("finding its overview by its new email address") {
                val overview = accountOverviews.findByEmailAddress(secondEmail)

                Then("it should be up to date") {
                    overview shouldBe expected
                }
            }

            When("finding an overview by its previous email address") {
                val overview = accountOverviews.findByEmailAddress(firstEmail)

                Then("it should find nothing") {
                    overview.shouldBeNull()
                }
            }
        }

        Given("an account that just changed its role") {
            val accountId = givenRegisteredAccount(firstEmail)
            accounts.append(
                accountId,
                accounts.load(accountId).version,
                listOf(event(accountId, Payload.RoleChanged(AccountRole.ADMIN))),
            ).shouldBeSuccess()

            When("finding its overview by account id") {
                val overview = accountOverviews.findById(accountId)

                Then("it should be up to date") {
                    overview shouldBe AccountOverview.from(accounts.load(accountId).events)
                }
            }
        }

        Given("an account whose append was rejected") {
            val accountId = givenRegisteredAccount(firstEmail)
            val expected = accountOverviews.findById(accountId)
            accounts.append(
                accountId,
                AccountVersion.INITIAL,
                listOf(event(accountId, Payload.RoleChanged(AccountRole.ADMIN))),
            ) shouldBeFailure AccountAppendFailure.VersionConflict

            When("finding its overview by account id") {
                val overview = accountOverviews.findById(accountId)

                Then("it should not change") {
                    overview shouldBe expected
                }
            }
        }

        Given("an account without events") {
            val accountId = AccountId.generate()
            val registered = registration(accountId, firstEmail)
            val promoted = event(accountId, Payload.RoleChanged(AccountRole.ADMIN))

            When("appending events at the initial version") {
                val outcome = accounts.append(accountId, AccountVersion.INITIAL, listOf(registered, promoted))

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("the events should be loaded in order") {
                    accounts.load(accountId).events shouldBe listOf(registered, promoted)
                }

                Then("the version should count the events") {
                    accounts.load(accountId).version shouldBe AccountVersion.from(2)
                }
            }
        }

        Given("a registered account and an outdated version") {
            val accountId = givenRegisteredAccount(firstEmail)
            val history = accounts.load(accountId)

            When("appending an event at this version") {
                val outcome = accounts.append(
                    accountId,
                    AccountVersion.INITIAL,
                    listOf(event(accountId, Payload.RoleChanged(AccountRole.ADMIN))),
                )

                Then("it should report a version conflict") {
                    outcome shouldBeFailure AccountAppendFailure.VersionConflict
                }

                Then("the history should not change") {
                    accounts.load(accountId) shouldBe history
                }
            }
        }

        Given("a registered account and a version ahead of its stream") {
            val accountId = givenRegisteredAccount(firstEmail)

            When("appending an event at this version") {
                val outcome = accounts.append(
                    accountId,
                    AccountVersion.from(5),
                    listOf(event(accountId, Payload.RoleChanged(AccountRole.ADMIN))),
                )

                Then("it should report a version conflict") {
                    outcome shouldBeFailure AccountAppendFailure.VersionConflict
                }
            }
        }

        Given("a registered account and no event to append") {
            val accountId = givenRegisteredAccount(firstEmail)
            val version = accounts.load(accountId).version

            When("appending") {
                Then("it should be rejected as a programming error") {
                    shouldThrow<IllegalArgumentException> { accounts.append(accountId, version, emptyList()) }
                }
            }
        }

        Given("a registered account and an event of another account") {
            val accountId = givenRegisteredAccount(firstEmail)
            val version = accounts.load(accountId).version
            val foreignEvent = event(AccountId.generate(), Payload.RoleChanged(AccountRole.ADMIN))

            When("appending") {
                Then("it should be rejected as a programming error") {
                    shouldThrow<IllegalArgumentException> { accounts.append(accountId, version, listOf(foreignEvent)) }
                }
            }
        }

        Given("an email address held by an account") {
            givenRegisteredAccount(firstEmail)
            val accountId = AccountId.generate()

            When("registering another account with it") {
                val outcome = accounts.append(accountId, AccountVersion.INITIAL, listOf(registration(accountId, firstEmail)))

                Then("it should report the email address as used") {
                    outcome shouldBeFailure AccountAppendFailure.EmailAddressAlreadyUsed(firstEmail)
                }

                Then("the other account should not be created") {
                    accounts.load(accountId).events.shouldBeEmpty()
                }
            }
        }

        Given("two accounts with different email addresses") {
            val accountId = givenRegisteredAccount(firstEmail)
            givenRegisteredAccount(secondEmail)
            val history = accounts.load(accountId)

            When("one account changes to the email address of the other") {
                val outcome = accounts.append(
                    accountId,
                    history.version,
                    listOf(event(accountId, Payload.EmailAddressChanged(secondEmail))),
                )

                Then("it should report the email address as used") {
                    outcome shouldBeFailure AccountAppendFailure.EmailAddressAlreadyUsed(secondEmail)
                }

                Then("the history should not change") {
                    accounts.load(accountId) shouldBe history
                }
            }
        }

        Given("an email address released by an account") {
            val previousOwnerId = givenRegisteredAccount(firstEmail)
            accounts.append(
                previousOwnerId,
                accounts.load(previousOwnerId).version,
                listOf(event(previousOwnerId, Payload.EmailAddressChanged(secondEmail))),
            ).shouldBeSuccess()
            val accountId = AccountId.generate()

            When("registering another account with it") {
                val outcome = accounts.append(accountId, AccountVersion.INITIAL, listOf(registration(accountId, firstEmail)))

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }
            }
        }
    }
}
