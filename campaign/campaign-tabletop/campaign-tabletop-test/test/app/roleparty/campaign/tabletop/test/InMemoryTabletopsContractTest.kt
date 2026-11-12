package app.roleparty.campaign.tabletop.test

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId

class InMemoryTabletopsContractTest : TabletopsContractTest({
    TabletopsContractSubject(InMemoryTabletops(), { CampaignId.generate() }, { GameSessionId.generate() })
})
