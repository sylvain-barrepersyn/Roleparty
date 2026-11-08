package app.roleparty.campaign.story.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId

class InMemoryScenesContractTest : ScenesContractTest({
    ScenesContractSubject(InMemoryScenes(), givenCampaign = { CampaignId.generate() }, givenLibraryEntry = { LibraryEntryId.generate() })
})
