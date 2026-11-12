package app.roleparty.campaign.tabletop.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.campaign.tabletop.test.TabletopBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.nulls.shouldBeNull

class StartedGameSessionFindingServiceTest : TabletopBehaviorTest() {

    init {
        Given("a campaign where no game session was started") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            fixtures.`given a game session`(campaignId)

            Then("its participants should find none") {
                fixtures.findStartedGameSession.ofCampaign(gameMaster, campaignId).shouldBeSuccess().shouldBeNull()
            }

            Then("a stranger or an unknown campaign should be refused") {
                fixtures.findStartedGameSession.ofCampaign(AccountId.generate(), campaignId) shouldBeFailure TabletopError.ParticipantRequired
                val unknown = CampaignId.generate()
                fixtures.findStartedGameSession.ofCampaign(gameMaster, unknown) shouldBeFailure TabletopError.CampaignNotFound(unknown)
            }
        }
    }
}
