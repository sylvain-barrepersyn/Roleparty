package app.roleparty.campaign.library.infrastructure

import app.roleparty.campaign.database.setUpCampaignSchema
import app.roleparty.campaign.library.domain.LibraryModule
import app.roleparty.campaign.library.infrastructure.adapter.primary.LibraryLocalization
import app.roleparty.campaign.library.infrastructure.adapter.primary.libraryRoutes
import app.roleparty.campaign.library.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.library.infrastructure.adapter.secondary.InfrastructuredLibraryEntries
import app.roleparty.core.database.Postgres
import app.roleparty.core.ktor.rest.resourceRouting
import io.ktor.server.application.Application

fun loadLibraryModule(postgres: Postgres): LibraryModule {
    postgres.setUpCampaignSchema()

    val database = postgres.database
    return LibraryModule(InfrastructuredLibraryEntries(database), InfrastructuredCampaigns(database))
}

fun Application.serveLibraryRoutes(module: LibraryModule, localization: LibraryLocalization) {
    resourceRouting { libraryRoutes(module, localization) }
}
