package app.roleparty.security.test

import app.roleparty.core.test.FixturedBehaviorTest

abstract class SecurityBehaviorTest : FixturedBehaviorTest() {

    protected val fixtures = SecurityFixture(clock)
}
