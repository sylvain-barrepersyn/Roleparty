package app.roleparty.campaign.agenda.test

import app.roleparty.campaign.commons.CampaignId

class InMemoryAgendaPreferencesContractTest : AgendaPreferencesContractTest({
    AgendaPreferencesContractSubject(InMemoryAgendaPreferences()) { CampaignId.generate() }
})
