package app.roleparty.campaign.agenda.test

import app.roleparty.campaign.commons.CampaignId

class InMemoryGameSessionsContractTest : GameSessionsContractTest({ GameSessionsContractSubject(InMemoryGameSessions()) { CampaignId.generate() } })
