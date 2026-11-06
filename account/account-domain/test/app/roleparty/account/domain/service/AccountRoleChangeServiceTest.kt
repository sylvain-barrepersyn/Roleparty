package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.commons.Requester
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.domain.port.primary.AccountRoleChangeCommand
import app.roleparty.account.test.AccountBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class AccountRoleChangeServiceTest : AccountBehaviorTest() {

    private val roleChange = fixtures.accountRoleChangeCommandHandler

    init {
        Given("an admin and a simple user") {
            val adminId = fixtures.`given an admin`()
            val userId = fixtures.`given an account`()

            When("the admin promotes the user to admin") {
                val outcome = roleChange.handle(accountId = userId, command = AccountRoleChangeCommand(Requester.from(adminId), AccountRole.ADMIN))

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("the user should be an admin") {
                    fixtures.overviewOf(userId).isAdmin shouldBe true
                }
            }
        }

        Given("two admins") {
            val adminId = fixtures.`given an admin`()
            val otherAdminId = fixtures.`given an admin`()

            When("one admin demotes the other") {
                val outcome = roleChange.handle(accountId = otherAdminId, command = AccountRoleChangeCommand(Requester.from(adminId), AccountRole.USER))

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("the other admin should be a simple user") {
                    fixtures.overviewOf(otherAdminId).role shouldBe AccountRole.USER
                }
            }
        }

        Given("two simple users") {
            val requesterId = fixtures.`given an account`()
            val userId = fixtures.`given an account`()

            When("one user promotes the other to admin") {
                val outcome = roleChange.handle(accountId = userId, command = AccountRoleChangeCommand(Requester.from(requesterId), AccountRole.ADMIN))

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AdminRoleRequired
                }
            }
        }

        Given("an unknown requester and a simple user") {
            val requester = Requester.from(AccountId.generate())
            val userId = fixtures.`given an account`()

            When("the requester promotes the user to admin") {
                val outcome = roleChange.handle(accountId = userId, command = AccountRoleChangeCommand(requester, AccountRole.ADMIN))

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AdminRoleRequired
                }
            }
        }

        Given("an admin") {
            val adminId = fixtures.`given an admin`()

            When("the admin changes their own role") {
                val outcome = roleChange.handle(accountId = adminId, command = AccountRoleChangeCommand(Requester.from(adminId), AccountRole.USER))

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.OwnRoleChangeForbidden
                }
            }
        }

        Given("an admin and an unknown account") {
            val adminId = fixtures.`given an admin`()
            val unknownId = AccountId.generate()

            When("the admin changes the role of the account") {
                val outcome = roleChange.handle(accountId = unknownId, command = AccountRoleChangeCommand(Requester.from(adminId), AccountRole.ADMIN))

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AccountNotFound(unknownId)
                }
            }
        }
    }
}
