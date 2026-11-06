package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.commons.Requester
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.test.AccountBehaviorTest
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class AccountServiceTest : AccountBehaviorTest() {

    private val useCases = fixtures.useCases
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val otherEmail = EmailAddress.from("legolas@roleparty.app")

    init {
        Given("an unused email address") {
            When("registering with it") {
                val accountId = useCases.register(email).shouldBeSuccess()

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
                val outcome = useCases.register(email)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.EmailAddressAlreadyUsed(email)
                }
            }
        }

        Given("an email address released by an account") {
            val previousOwner = fixtures.`given an account`(email)
            useCases.changeEmailAddress(previousOwner, otherEmail).shouldBeSuccess()

            When("registering with it") {
                val outcome = useCases.register(email)

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }
            }
        }

        Given("a registered account and an unused email address") {
            val accountId = fixtures.`given an account`(email)

            When("changing to this email address") {
                val outcome = useCases.changeEmailAddress(accountId, otherEmail)

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
                val outcome = useCases.changeEmailAddress(accountId, email)

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
                val outcome = useCases.changeEmailAddress(accountId, otherEmail)

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
                val outcome = useCases.changeEmailAddress(accountId, email)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AccountNotFound(accountId)
                }
            }

            When("finding it") {
                Then("it should find nothing") {
                    useCases.findById(accountId).shouldBeNull()
                    useCases.findByEmailAddress(email).shouldBeNull()
                }
            }
        }

        Given("an account to find") {
            val accountId = fixtures.`given an account`(email)

            When("finding it") {
                Then("it should find it by id and by email address") {
                    useCases.findById(accountId) shouldBe fixtures.accountOf(accountId)
                    useCases.findByEmailAddress(email) shouldBe fixtures.accountOf(accountId)
                }
            }
        }

        Given("an admin and a simple user") {
            val adminId = fixtures.`given an admin`()
            val userId = fixtures.`given an account`()

            When("the admin promotes the user to admin") {
                val outcome = useCases.changeRole(Requester.from(adminId), userId, AccountRole.ADMIN)

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("the user should be an admin") {
                    fixtures.accountOf(userId).isAdmin shouldBe true
                }
            }
        }

        Given("an admin and a user already having the role") {
            val adminId = fixtures.`given an admin`()
            val userId = fixtures.`given an account`()
            val writes = fixtures.writes()

            When("the admin gives the user their current role") {
                val outcome = useCases.changeRole(Requester.from(adminId), userId, AccountRole.USER)

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("nothing should be written, so that no event is published") {
                    fixtures.writes() shouldBe writes
                }
            }
        }

        Given("two admins") {
            val adminId = fixtures.`given an admin`()
            val otherAdminId = fixtures.`given an admin`()

            When("one admin demotes the other") {
                val outcome = useCases.changeRole(Requester.from(adminId), otherAdminId, AccountRole.USER)

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("the other admin should be a simple user") {
                    fixtures.accountOf(otherAdminId).role shouldBe AccountRole.USER
                }
            }
        }

        Given("two simple users") {
            val userId = fixtures.`given an account`()
            val otherUserId = fixtures.`given an account`()

            When("one user promotes the other to admin") {
                val outcome = useCases.changeRole(Requester.from(userId), otherUserId, AccountRole.ADMIN)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AdminRoleRequired
                }
            }
        }

        Given("an unknown requester and a simple user") {
            val userId = fixtures.`given an account`()

            When("the requester promotes the user to admin") {
                val outcome = useCases.changeRole(Requester.from(AccountId.generate()), userId, AccountRole.ADMIN)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AdminRoleRequired
                }
            }
        }

        Given("an admin") {
            val adminId = fixtures.`given an admin`()

            When("the admin changes their own role") {
                val outcome = useCases.changeRole(Requester.from(adminId), adminId, AccountRole.USER)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.OwnRoleChangeForbidden
                }
            }
        }

        Given("an admin and an unknown account") {
            val adminId = fixtures.`given an admin`()
            val accountId = AccountId.generate()

            When("the admin changes the role of the account") {
                val outcome = useCases.changeRole(Requester.from(adminId), accountId, AccountRole.ADMIN)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AccountNotFound(accountId)
                }
            }
        }
    }
}
