package app.roleparty.account.test

import app.roleparty.core.test.FixturedBehaviorTest

abstract class AccountBehaviorTest : FixturedBehaviorTest() {

    protected val fixtures = AccountFixtures(clock)
}
