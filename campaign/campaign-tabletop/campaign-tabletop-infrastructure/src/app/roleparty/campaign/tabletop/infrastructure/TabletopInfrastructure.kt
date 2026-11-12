package app.roleparty.campaign.tabletop.infrastructure

import app.roleparty.campaign.database.setUpCampaignSchema
import app.roleparty.campaign.tabletop.contract.web.IceServerBody
import app.roleparty.campaign.tabletop.domain.TabletopModule
import app.roleparty.campaign.tabletop.infrastructure.adapter.primary.TabletopHub
import app.roleparty.campaign.tabletop.infrastructure.adapter.primary.TabletopLocalization
import app.roleparty.campaign.tabletop.infrastructure.adapter.primary.tabletopRoutes
import app.roleparty.campaign.tabletop.infrastructure.adapter.secondary.InfrastructuredCampaigns
import app.roleparty.campaign.tabletop.infrastructure.adapter.secondary.InfrastructuredGameSessions
import app.roleparty.campaign.tabletop.infrastructure.adapter.secondary.InfrastructuredPlayerCharacters
import app.roleparty.campaign.tabletop.infrastructure.adapter.secondary.InfrastructuredTabletops
import app.roleparty.core.commons.valueOrFile
import app.roleparty.core.database.Postgres
import app.roleparty.core.ktor.rest.resourceRouting
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.websocket.WebSockets

private const val ICE_SERVER_URLS = "TABLETOP_ICE_SERVER_URLS"
private const val ICE_SERVER_USERNAME = "TABLETOP_ICE_SERVER_USERNAME"
private const val ICE_SERVER_CREDENTIAL = "TABLETOP_ICE_SERVER_CREDENTIAL"
private const val DEFAULT_ICE_SERVER_URLS = "stun:stun.l.google.com:19302"

fun loadTabletopModule(postgres: Postgres): TabletopModule {
    postgres.setUpCampaignSchema()

    val database = postgres.database
    return TabletopModule(
        tabletops = InfrastructuredTabletops(database),
        gameSessions = InfrastructuredGameSessions(database),
        campaigns = InfrastructuredCampaigns(database),
        playerCharacters = InfrastructuredPlayerCharacters(database),
    )
}

fun Application.serveTabletopRoutes(module: TabletopModule, localization: TabletopLocalization, env: Map<String, String> = System.getenv()) {
    pluginOrNull(WebSockets) ?: install(WebSockets)
    val hub = TabletopHub()
    val iceServers = iceServersOf(env)
    resourceRouting { tabletopRoutes(module, hub, iceServers, localization) }
}

internal fun iceServersOf(env: Map<String, String>): List<IceServerBody> {
    val urls = (env.valueOrFile(ICE_SERVER_URLS) ?: DEFAULT_ICE_SERVER_URLS).split(',').map(String::trim).filter(String::isNotEmpty)
    if (urls.isEmpty()) return emptyList()
    return listOf(IceServerBody(urls, env.valueOrFile(ICE_SERVER_USERNAME), env.valueOrFile(ICE_SERVER_CREDENTIAL)))
}
