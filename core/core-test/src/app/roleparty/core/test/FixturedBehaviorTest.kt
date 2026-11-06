package app.roleparty.core.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec

abstract class FixturedBehaviorTest : BehaviorSpec() {

    override fun isolationMode() = IsolationMode.InstancePerRoot

    protected val clock = ControlledClock()
}
