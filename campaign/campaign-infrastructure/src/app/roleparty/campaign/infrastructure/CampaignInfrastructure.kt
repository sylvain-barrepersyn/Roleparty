package app.roleparty.campaign.infrastructure

import app.roleparty.campaign.database.setUpCampaignSchema
import app.roleparty.campaign.domain.CampaignModule
import app.roleparty.campaign.infrastructure.adapter.primary.CampaignLocalization
import app.roleparty.campaign.infrastructure.adapter.primary.campaignRoutes
import app.roleparty.campaign.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.infrastructure.adapter.secondary.InfrastructuredGameSessions
import app.roleparty.campaign.infrastructure.adapter.secondary.InfrastructuredInvitations
import app.roleparty.core.database.Postgres
import io.ktor.server.application.Application
import app.roleparty.core.ktor.rest.resourceRouting
import kotlin.time.Clock

fun loadCampaignModule(postgres: Postgres, clock: Clock = Clock.System): CampaignModule {
    postgres.setUpCampaignSchema()

    val database = postgres.database
    return CampaignModule(
        campaigns = InfrastructuredCampaigns(database, clock),
        invitations = InfrastructuredInvitations(database),
        gameSessions = InfrastructuredGameSessions(database),
    )
}

fun Application.serveCampaignRoutes(module: CampaignModule, localization: CampaignLocalization) {
    resourceRouting { campaignRoutes(module, localization) }
}
