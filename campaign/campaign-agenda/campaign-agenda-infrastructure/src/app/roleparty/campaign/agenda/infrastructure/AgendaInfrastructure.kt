package app.roleparty.campaign.agenda.infrastructure

import app.roleparty.campaign.database.setUpCampaignSchema
import app.roleparty.campaign.agenda.domain.AgendaModule
import app.roleparty.campaign.agenda.infrastructure.adapter.primary.AgendaLocalization
import app.roleparty.campaign.agenda.infrastructure.adapter.primary.agendaRoutes
import app.roleparty.campaign.agenda.infrastructure.adapter.secondary.InfrastructuredAgendaPreferences
import app.roleparty.campaign.agenda.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.agenda.infrastructure.adapter.secondary.InfrastructuredGameSessions
import app.roleparty.campaign.agenda.infrastructure.adapter.secondary.InfrastructuredPlayerCharacters
import app.roleparty.core.database.Postgres
import io.ktor.server.application.Application
import app.roleparty.core.ktor.rest.resourceRouting

fun loadAgendaModule(postgres: Postgres): AgendaModule {
    postgres.setUpCampaignSchema()

    val database = postgres.database
    return AgendaModule(
        gameSessions = InfrastructuredGameSessions(database),
        campaigns = InfrastructuredCampaigns(database),
        playerCharacters = InfrastructuredPlayerCharacters(database),
        agendaPreferences = InfrastructuredAgendaPreferences(database),
    )
}

fun Application.serveAgendaRoutes(module: AgendaModule, localization: AgendaLocalization) {
    resourceRouting { agendaRoutes(module, localization) }
}
