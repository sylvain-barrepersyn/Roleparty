package app.roleparty.campaign.library.test

import app.roleparty.campaign.commons.CampaignId

class InMemoryLibraryEntriesContractTest : LibraryEntriesContractTest({ LibraryEntriesContractSubject(InMemoryLibraryEntries()) { CampaignId.generate() } })
