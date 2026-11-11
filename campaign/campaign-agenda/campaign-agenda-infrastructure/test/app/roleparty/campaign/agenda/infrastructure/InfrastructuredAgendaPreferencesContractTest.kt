package app.roleparty.campaign.agenda.infrastructure

import app.roleparty.campaign.agenda.infrastructure.adapter.secondary.InfrastructuredAgendaPreferences
import app.roleparty.campaign.agenda.test.AgendaPreferencesContractSubject
import app.roleparty.campaign.agenda.test.AgendaPreferencesContractTest

class InfrastructuredAgendaPreferencesContractTest : AgendaPreferencesContractTest({
    AgendaPreferencesContractSubject(InfrastructuredAgendaPreferences(TestPostgres.database)) { TestPostgres.givenCampaign() }
})
