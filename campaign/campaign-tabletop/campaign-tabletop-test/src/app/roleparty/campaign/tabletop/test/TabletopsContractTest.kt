package app.roleparty.campaign.tabletop.test

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.domain.model.Tabletop
import app.roleparty.campaign.tabletop.domain.port.secondary.Tabletops
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.time.Instant

class TabletopsContractSubject(
    val tabletops: Tabletops,
    val givenCampaign: suspend () -> CampaignId,
    val givenGameSession: suspend (CampaignId) -> GameSessionId,
)

abstract class TabletopsContractTest(createSubject: () -> TabletopsContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()
    private val tabletops = subject.tabletops

    init {
        Given("a campaign whose game session was started") {
            val campaignId = subject.givenCampaign()
            val first = Tabletop(campaignId, subject.givenGameSession(campaignId), Instant.parse("2026-11-12T19:00:00Z"))
            tabletops.save(first)

            Then("its tabletop should be found, and none for another campaign") {
                tabletops.ofCampaign(campaignId) shouldBe first
                tabletops.ofCampaign(subject.givenCampaign()).shouldBeNull()
            }

            When("another game session is started") {
                val second = Tabletop(campaignId, subject.givenGameSession(campaignId), Instant.parse("2026-11-19T19:00:00Z"))
                tabletops.save(second)

                Then("the tabletop should be on the latest one") {
                    tabletops.ofCampaign(campaignId) shouldBe second
                }
            }
        }
    }
}
