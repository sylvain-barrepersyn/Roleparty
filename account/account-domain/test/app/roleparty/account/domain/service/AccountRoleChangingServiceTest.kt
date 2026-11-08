package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.commons.Requester
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.test.AccountBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class AccountRoleChangingServiceTest : AccountBehaviorTest() {

    private val changeAccountRole = fixtures.changeAccountRole

    init {
        Given("an admin and a simple user") {
            val adminId = fixtures.`given an admin`()
            val userId = fixtures.`given an account`()

            When("the admin promotes the user to admin") {
                val outcome = changeAccountRole(Requester.from(adminId), userId, AccountRole.ADMIN)

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
                val outcome = changeAccountRole(Requester.from(adminId), userId, AccountRole.USER)

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
                val outcome = changeAccountRole(Requester.from(adminId), otherAdminId, AccountRole.USER)

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
                val outcome = changeAccountRole(Requester.from(userId), otherUserId, AccountRole.ADMIN)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AdminRoleRequired
                }
            }
        }

        Given("an unknown requester and a simple user") {
            val userId = fixtures.`given an account`()

            When("the requester promotes the user to admin") {
                val outcome = changeAccountRole(Requester.from(AccountId.generate()), userId, AccountRole.ADMIN)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AdminRoleRequired
                }
            }
        }

        Given("an admin") {
            val adminId = fixtures.`given an admin`()

            When("the admin changes their own role") {
                val outcome = changeAccountRole(Requester.from(adminId), adminId, AccountRole.USER)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.OwnRoleChangeForbidden
                }
            }
        }

        Given("an admin and an unknown account") {
            val adminId = fixtures.`given an admin`()
            val accountId = AccountId.generate()

            When("the admin changes the role of the account") {
                val outcome = changeAccountRole(Requester.from(adminId), accountId, AccountRole.ADMIN)

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AccountNotFound(accountId)
                }
            }
        }
    }
}
