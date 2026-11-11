package app.roleparty.campaign.creature.infrastructure

import app.roleparty.campaign.database.setUpCampaignSchema
import app.roleparty.campaign.creature.domain.CreatureModule
import app.roleparty.campaign.creature.infrastructure.adapter.primary.CreatureLocalization
import app.roleparty.campaign.creature.infrastructure.adapter.primary.creatureRoutes
import app.roleparty.campaign.creature.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.creature.infrastructure.adapter.secondary.InfrastructuredCharacterSheets
import app.roleparty.campaign.creature.infrastructure.adapter.secondary.InfrastructuredMonsterSheets
import app.roleparty.campaign.creature.infrastructure.adapter.secondary.InfrastructuredPlayerCharacters
import app.roleparty.core.database.Postgres
import io.ktor.server.application.Application
import app.roleparty.core.ktor.rest.resourceRouting

fun loadCreatureModule(postgres: Postgres): CreatureModule {
    postgres.setUpCampaignSchema()

    val database = postgres.database
    return CreatureModule(
        monsterSheets = InfrastructuredMonsterSheets(database),
        campaigns = InfrastructuredCampaigns(database),
        characterSheets = InfrastructuredCharacterSheets(database),
        playerCharacters = InfrastructuredPlayerCharacters(database),
    )
}

fun Application.serveCreatureRoutes(module: CreatureModule, localization: CreatureLocalization) {
    resourceRouting { creatureRoutes(module, localization) }
}
