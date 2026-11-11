package app.roleparty.campaign.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

abstract class GameSessionsContractTest(createStores: () -> CampaignStores) : FixturedBehaviorTest() {

    private val stores = createStores()
    private val gameSessions = stores.gameSessions

    init {
        Given("campaigns with past and future game sessions") {
            val today = LocalDate(2026, 11, 9)
            val scheduled = CampaignSamples.campaign().also { stores.campaigns.create(it) }
            val past = CampaignSamples.campaign().also { stores.campaigns.create(it) }
            val other = CampaignSamples.campaign().also { stores.campaigns.create(it) }
            stores.scheduleGameSession(scheduled.id, LocalDate(2026, 12, 1))
            stores.scheduleGameSession(scheduled.id, today)
            stores.scheduleGameSession(scheduled.id, LocalDate(2026, 11, 1))
            stores.scheduleGameSession(past.id, LocalDate(2026, 11, 8))
            stores.scheduleGameSession(other.id, LocalDate(2026, 11, 10))

            Then("the next date of each requested campaign should be found from the given day") {
                gameSessions.nextDates(listOf(scheduled.id, past.id), today) shouldBe mapOf(scheduled.id to today)
            }

            Then("no campaign should find nothing") {
                gameSessions.nextDates(emptyList<CampaignId>(), today).shouldBeEmpty()
            }
        }
    }
}
