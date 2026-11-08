package app.roleparty.campaign.agenda.test

import app.roleparty.core.test.FixturedBehaviorTest

abstract class AgendaBehaviorTest : FixturedBehaviorTest() {

    protected val fixtures = AgendaFixture(clock)
}
