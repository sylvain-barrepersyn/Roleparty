package app.roleparty.campaign.tabletop.test

import app.roleparty.core.test.FixturedBehaviorTest

abstract class TabletopBehaviorTest : FixturedBehaviorTest() {

    protected val fixtures = TabletopFixture(clock)
}
