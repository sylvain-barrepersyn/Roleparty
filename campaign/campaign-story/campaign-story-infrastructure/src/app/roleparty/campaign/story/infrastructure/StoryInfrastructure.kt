package app.roleparty.campaign.story.infrastructure

import app.roleparty.campaign.database.setUpCampaignSchema
import app.roleparty.campaign.story.domain.StoryModule
import app.roleparty.campaign.story.infrastructure.adapter.primary.StoryLocalization
import app.roleparty.campaign.story.infrastructure.adapter.primary.storyRoutes
import app.roleparty.campaign.story.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.story.infrastructure.adapter.secondary.InfrastructuredLibraryEntries
import app.roleparty.campaign.story.infrastructure.adapter.secondary.InfrastructuredScenes
import app.roleparty.core.database.Postgres
import app.roleparty.core.ktor.rest.resourceRouting
import io.ktor.server.application.Application

fun loadStoryModule(postgres: Postgres): StoryModule {
    postgres.setUpCampaignSchema()

    val database = postgres.database
    return StoryModule(InfrastructuredScenes(database), InfrastructuredCampaigns(database), InfrastructuredLibraryEntries(database))
}

fun Application.serveStoryRoutes(module: StoryModule, localization: StoryLocalization) {
    resourceRouting { storyRoutes(module, localization) }
}
